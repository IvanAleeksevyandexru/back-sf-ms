package ru.gosuslugi.pgu.xmlservice.context.service.impl;

import static java.util.Objects.isNull;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.common.core.exception.EntityNotFoundException;
import ru.gosuslugi.pgu.common.core.exception.JsonParsingException;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ApplicantAnswer;
import ru.gosuslugi.pgu.dto.ApplicantDto;
import ru.gosuslugi.pgu.dto.Descriptor;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.ServiceInfoDto;
import ru.gosuslugi.pgu.dto.SpDescriptionSection;
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient;
import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.context.service.TemplateDataService;
import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@inheritDoc}
 * <p>
 * В качестве источников данных используется черновик, запрашиваемый у сервиса черновиков, и
 * информация об услуге, запрашиваемая у service-descriptor-storage.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateDataServiceImpl implements TemplateDataService {
    private static final ObjectMapper OBJECT_MAPPER = JsonProcessingUtil.getObjectMapper();
    private static final TypeReference<Object> OBJ_TYPE_REF = new TypeReference<>() {};
    private final ServiceDescriptorClient serviceDescriptorClient;
    private final DraftClient draftClient;
    private final AttachmentService attachmentService;
    @Value("${service-descriptor-storage-client.integration:#{false}}")
    private boolean sdIntegrationEnabled;

    @Override
    public TemplateDataContext prepare(GenerateXmlRequest request) {
        final ScenarioDto draft = loadDraft(request);
        TemplateDataContext dataContext = initFromRequest(request);
        SpDescriptionSection serviceDescription = null;
        if (sdIntegrationEnabled) {
            serviceDescription = getServiceDescriptor(draft.getServiceDescriptorId());
            populateFromServiceDescriptor(dataContext, serviceDescription);
        }
        populateFromDraft(dataContext, request, draft, serviceDescription);
        return dataContext;
    }

    public void populateFromDraft(final TemplateDataContext dataContext,
            final GenerateXmlRequest request, final ScenarioDto draft,
            final SpDescriptionSection serviceDescription) {

        dataContext.getValues().clear();
        dataContext.getValues().putAll(getApplicantAnswers(draft));
        dataContext.getValues().put(TemplateDataContext.ROLE_KEY, request.getRoleId());

        dataContext.getAdditionalValues().clear();
        dataContext.getAdditionalValues()
                   .putAll(getAdditionalValues(draft, request.getUserId(), serviceDescription));
    }

    private void populateFromServiceDescriptor(TemplateDataContext dataContext,
            final SpDescriptionSection spDescriptionSection) {
        if (Objects.nonNull(spDescriptionSection)
                && Objects.nonNull(spDescriptionSection.getParameters())) {
            dataContext.getServiceParameters().putAll(spDescriptionSection.getParameters());
        }
    }

    private TemplateDataContext initFromRequest(GenerateXmlRequest request) {
        TemplateDataContext dataContext = new TemplateDataContext();
        dataContext.setServiceId(request.getServiceId());
        dataContext.setOrderId(request.getOrderId());
        dataContext.setOid(request.getUserId());
        dataContext.setFileDescription(request.getFileDescription());
        return dataContext;
    }

    private ScenarioDto loadDraft(GenerateXmlRequest request) {
        if (Objects.nonNull(request.getDraft())) {
            return request.getDraft();
        }
        DraftHolderDto draftHolder = draftClient.getDraftById(
                request.getOrderId(), request.getUserId(), request.getOrgId());
        if (isNull(draftHolder) || isNull(draftHolder.getBody())) {
            throw new EntityNotFoundException(
                    "Черновик, необходимый для формирования данных для XML, не найден: " + request);
        }
        return draftHolder.getBody();
    }

    private Map<String, Object> getApplicantAnswers(ScenarioDto draft) {
        final Map<String, ApplicantAnswer> draftApplicantAnswers =
                getApplicantAnswersWithCurrentFromDraft(draft);

        return draftApplicantAnswers.entrySet()
                                    .stream()
                                    .filter(entry -> Optional.ofNullable(entry.getValue())
                                                             .map(ApplicantAnswer::getValue)
                                                             .isPresent())
                                    .collect(Collectors.toMap(Entry::getKey,
                                            entry -> toJavaBeanIfNeeded(entry.getKey(),
                                                    entry.getValue().getValue())));
    }

    private Map<String, ApplicantAnswer> getApplicantAnswersWithCurrentFromDraft(
            ScenarioDto draft) {
        final Map<String, ApplicantAnswer> draftApplicantAnswers =
                new HashMap<>(draft.getApplicantAnswers());
        draftApplicantAnswers.putAll(draft.getCurrentValue());
        return draftApplicantAnswers;
    }

    private Map<String, Object> getAdditionalValues(ScenarioDto draft, Long oid,
            SpDescriptionSection serviceDescription) {
        Map<String, Object> additionalValues = new HashMap<>(draft.getAdditionalParameters());
        checkAndPopulateSpRequestKeys(additionalValues);

        additionalValues.put(TemplateDataContext.SERVICE_INFO_KEY,
                convertServiceInfoToMap(draft.getServiceInfo()));

        ApplicantDto currentParticipant = draft.getParticipants().get(oid.toString());
        CycledComponentPosition cycledAnswersPos = computeCycledAnswerPosition(currentParticipant);
        additionalValues.put(TemplateDataContext.ROLE_COMPONENT_KEY,
                cycledAnswersPos.getComponentName());
        additionalValues.put(TemplateDataContext.ROLE_INDEX_KEY, cycledAnswersPos.getItemIndex());

        String cycledComponentValue = null;
        if (cycledAnswersPos.isComponentNameDefined()) {
            cycledComponentValue =
                    Optional.ofNullable(draft.getApplicantAnswers()
                                             .get(cycledAnswersPos.getComponentName()))
                            .map(ApplicantAnswer::getValue)
                            .orElse(null);
        }

        if (Objects.nonNull(cycledComponentValue) && cycledAnswersPos.isItemIndexDefined()) {
            Map cycledApplicantAnswers =
                    extractCycledParticipantAnswers(cycledComponentValue, cycledAnswersPos);
            additionalValues.putAll(cycledApplicantAnswers);
        }

        additionalValues.putAll(attachmentService.getAttachmentsDigestValues(draft,
                isDefaultDigestValueEnabled(serviceDescription)));
        return additionalValues;
    }

    private CycledComponentPosition computeCycledAnswerPosition(ApplicantDto currentParticipant) {
        final String cycledComponentName = Optional.ofNullable(currentParticipant)
                                                   .map(ApplicantDto::getComponent)
                                                   .orElse(null);

        final Integer participantCycledIndex =
                Optional.ofNullable(currentParticipant)
                        .map(ApplicantDto::getIndex).orElse(null);

        CycledComponentPosition cycledAnswersPos =
                new CycledComponentPosition(cycledComponentName, participantCycledIndex);
        return cycledAnswersPos;
    }

    private Map extractCycledParticipantAnswers(String cycledComponentValue,
            CycledComponentPosition cycledAnswerPos) {
        Map cycledApplicantAnswers = Collections.emptyMap();
        if (hasNoJsonSigns(cycledComponentValue)) {
            log.warn("Тип данных ответа созаявителя"
                    + " не поддерживается (не является JSON-массивом или JSON-объектом)");
            return cycledApplicantAnswers;
        }
        try {
            Object mappedAnswers = OBJECT_MAPPER.readValue(cycledComponentValue, OBJ_TYPE_REF);
            if (!(mappedAnswers instanceof ArrayList)) {
                log.warn("Контейнер ответов циклического компонента {} должен являться "
                        + "JSON-массивом. Игнорирую", cycledAnswerPos.getComponentName());
                return cycledApplicantAnswers;
            }

            List<?> answers = (ArrayList<?>) mappedAnswers;
            final Integer itemIndex = cycledAnswerPos.getItemIndex();
            if (itemIndex >= answers.size()) {
                log.warn("Индекс элемента {} в циклическом компоненте {} не существует. Игнорирую",
                        cycledAnswerPos.getItemIndex(), cycledAnswerPos.getComponentName());
                return cycledApplicantAnswers;
            }

            Object cycledApplicantItemValue = answers.get(itemIndex);
            if (Objects.isNull(cycledApplicantItemValue)
                    || !Map.class.isAssignableFrom(cycledApplicantItemValue.getClass())) {
                log.warn("Значение циклического компонента {} имеет неверный формат. Игнорирую",
                        cycledAnswerPos.getComponentName());
                return cycledApplicantAnswers;
            }
            cycledApplicantAnswers = (Map) cycledApplicantItemValue;
        } catch (IOException e) {
            log.error("Невозможно обработать ответы циклического компонента созаявителя", e);
        }
        return cycledApplicantAnswers;
    }

    private void checkAndPopulateSpRequestKeys(Map<String, Object> additionalValues) {
        if (!additionalValues.containsKey(TemplateDataContext.SP_REQUEST_GUID_KEY)) {
            String requestGuid = UUID.randomUUID().toString();
            additionalValues.put(TemplateDataContext.SP_REQUEST_GUID_KEY, requestGuid);
        }
        if (!additionalValues.containsKey(TemplateDataContext.SP_REQUEST_HASH_KEY)) {
            String requestHash = UUID.randomUUID().toString();
            additionalValues.put(TemplateDataContext.SP_REQUEST_HASH_KEY, requestHash);
        }
    }

    private Object toJavaBeanIfNeeded(String key, String origValue) {
        if (Objects.isNull(origValue) || hasNoJsonSigns(origValue)) {
            return origValue;
        }
        Object mappedValue;
        try {
            mappedValue = JsonProcessingUtil.fromJson(origValue, OBJ_TYPE_REF);
        } catch (JsonParsingException e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Невозможно конвертировать в объект значение атрибута с id {}; содержимое "
                                + "атрибута: {}. Сведения об ошибке: {}", key, origValue,
                        e.getMessage(), e);
            }
            mappedValue = origValue;
        }
        return mappedValue;
    }

    private Map convertServiceInfoToMap(ServiceInfoDto serviceInfo) {
        Map map = null;
        try {
            map = OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsBytes(serviceInfo), Map.class);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Ошибка при конвертации ServiceInfoDto в хэщ-таблицу", e);
            }
        }
        return map;
    }

    /**
     * Включена ли опция подстановки дефолтного значения digestValue для сценария.
     *
     * @param spDescriptionSection описание сценария.
     * @return включено true, иначе false.
     */
    private boolean isDefaultDigestValueEnabled(SpDescriptionSection spDescriptionSection) {
        return Optional.ofNullable(spDescriptionSection)
                       .map(SpDescriptionSection::getSpConfig)
                       .map(Descriptor::isDefaultDigestValueEnabled)
                       .orElse(true);
    }

    private SpDescriptionSection getServiceDescriptor(String serviceId) {
        val descriptorString = serviceDescriptorClient.getServiceDescriptor(serviceId);
        log.info("Получено описание услуги с id {}: {}", serviceId, descriptorString);
        return JsonProcessingUtil.fromJson(descriptorString, SpDescriptionSection.class);
    }

    private boolean hasNoJsonSigns(String value) {
        return !value.contains("{") && !value.contains("[");
    }

    @Data
    static class CycledComponentPosition {
        private final String componentName;
        private final Integer itemIndex;

        public boolean isComponentNameDefined() {
            return StringUtils.isNoneBlank(componentName);
        }

        public boolean isItemIndexDefined() {
            return Objects.nonNull(itemIndex) && itemIndex >= 0;
        }
    }
}
