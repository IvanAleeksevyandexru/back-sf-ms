package ru.gosuslugi.pgu.sp.adapter.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.dto.ApplicantAnswer;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.AttachmentInfo;
import ru.gosuslugi.pgu.dto.Descriptor;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.ServiceInfoDto;
import ru.gosuslugi.pgu.dto.SpDescriptionSection;
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient;
import ru.gosuslugi.pgu.sp.adapter.client.SpServiceDescriptorClient;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatesDataContextService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


@Service
@RequiredArgsConstructor
@Slf4j
public class TemplatesDataContextServiceImpl implements TemplatesDataContextService {

    protected static final String SERVICE_INFO_KEY = "serviceInfo";
    protected static final String ROLE_INDEX_ATTR_NAME = "roleIndex";
    protected static final String ROLE_COMPONENT_ATTR_NAME = "roleComponent";

    public static final String ADITIONAL_PARAM_FILE_MNEMONIC_KEY = "mnemonic";
    public static final String SP_REQUEST_GUID = "sp_request_guid";
    public static final String SP_REQUEST_HASH = "sp_request_hash";
    public static final String SYSTEM_AUTHORITY_ATTR_NAME = "systemAuthority";
    public static final String ORG_ID_ATTR_NAME = "orgId";
    public static final String ORG_TYPE_ATTR_NAME = "orgType";

    private final ObjectMapper mapper;

    private final AttachmentService attachmentService;
    private final ServiceDescriptorClient serviceDescriptorClient;
    private final SpServiceDescriptorClient spServiceDescriptorClient;

    @Data
    public static class IdCalculator{
        Map<String, Object> arguments;
    }

    @Override
    public TemplatesDataContext prepareRequestParameters(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft, Long orgId, Boolean skip17Status) {
        TemplatesDataContext dataContext = prepareDataContext(serviceId, orderId, oid, roleId, draft);
        dataContext.setOrgId(orgId);
        dataContext.setSkip17Status(skip17Status);
        return dataContext;
    }

    @Override
    public TemplatesDataContext prepareRequestParametersWithoutRemoveOldFiles(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft, Long orgId, Boolean skip17Status) {
        TemplatesDataContext dataContext = prepareDataContext(serviceId, orderId, oid, roleId, draft);
        dataContext.setOrgId(orgId);
        dataContext.setSkip17Status(skip17Status);
        return dataContext;
    }

    @Override
    public TemplatesDataContext prepareDataContext(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft) {
        TemplatesDataContext dataContext = prepareDraftParameters(serviceId, orderId, oid, roleId, draft);
        SpDescriptionSection spDescriptionSection = getDescriptor(draft.getServiceDescriptorId());

        Descriptor descriptor = spServiceDescriptorClient.getSpConfig(draft.getServiceDescriptorId());
        if (descriptor != null) {
            dataContext.setBusinessXmlName(descriptor.getBusinessXmlName());
            dataContext.setServiceCustomId(computeCustomServiceId(descriptor.getServiceCustomId(), draft));
            dataContext.setReplacedHeaders(descriptor.getReplacedHeaders());
            dataContext.setAlwaysAttachServicePdf(descriptor.getAlwaysAttachServicePdf());
            dataContext.setFiles(descriptor.getFiles());
            dataContext.setAdditionalPdfs(descriptor.getAdditionalPdfName());
            dataContext.setRegPreviewSendToSP(descriptor.isRegPreviewSendToSP());
        }

        Map<String, String> parameters = spServiceDescriptorClient.getSpParams(draft.getServiceDescriptorId());
        if (parameters != null) {
            dataContext.getServiceParameters().putAll(parameters);
        }
        logIllegalBusinessXmlName(serviceId, spDescriptionSection);
        return dataContext;
    }

    /**
     * Вычисление значения строки, на которую меняется serviceId при вызове sp
     *
     * @param serviceCustomId либо ссылка на компонент в applicantAnswers, либо ID
     * @param draft           информация по пройденнымм экранам
     * @return ссылка, на которую меняется serviceId при вызове sp
     */
    private String computeCustomServiceId(String serviceCustomId, ScenarioDto draft) {
        if (StringUtils.isEmpty(serviceCustomId)) {
            return serviceCustomId;
        }
        ApplicantAnswer answer = draft.getApplicantAnswers().get(serviceCustomId);
        if (answer != null) {
            String answerStrinig = answer.getValue();
            String unescaped = StringEscapeUtils.unescapeJava(answerStrinig);
            ObjectMapper mapper = new ObjectMapper();
            try {
                IdCalculator calculator = mapper.readValue(unescaped, IdCalculator.class);
                Object resultId = calculator.getArguments().get("serviceCustomId");
                if(resultId != null){
                    return resultId.toString();
                }
            } catch (JsonProcessingException e) {
                //Преобразования не удались. Там не искомый Json. Возвращаем значение из serviceCustomId
                if(log.isDebugEnabled()){
                    log.debug("Can not parse json for serviceCustomId, json is: ", answerStrinig);
                }
            }
        }
        return serviceCustomId;
    }

    private void logIllegalBusinessXmlName(String serviceId, SpDescriptionSection spDescriptionSection) {
        String businessXmlFilename = Optional.ofNullable(spDescriptionSection)
                .map(SpDescriptionSection::getSpConfig)
                .map(Descriptor::getBusinessXmlName).orElse(null);
        if (Objects.equals(serviceId, "10000000104") && Objects.isNull(businessXmlFilename)) {
            log.error("For serviceId {} property 'businessXmlName' is null. SP description section: {}", serviceId, spDescriptionSection);
        }
    }

    @Override
    public TemplatesDataContext prepareDraftParameters(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft) {
        TemplatesDataContext dataContext = new TemplatesDataContext();

        dataContext.setServiceId(serviceId);
        dataContext.setOrderId(orderId);
        dataContext.setRoleId(ApplicantRole.valueOf(roleId));
        dataContext.setOid(oid);
        if(Objects.nonNull(draft.getAdditionalParameters().getOrDefault("authorityId",null))){
            dataContext.setAuthorityId(draft.getAdditionalParameters().get("authorityId"));
        }

        //setting values
        dataContext.setValues(retrieveParametersFromDraft(draft));
        dataContext.getValues().put("role", roleId);

        //setting additional parameters
        dataContext.setAdditionalValues(retrieveAdditionalParametersFromDraft(draft, oid));
        val reusePaymentUin = dataContext.getAdditionalValues().get("reusePaymentUin");
        if (reusePaymentUin != null)
            dataContext.setReusePaymentUin(reusePaymentUin.toString());

        //prepare attachments
        dataContext.setAttachments(retrieveAttachmentParameters(draft,dataContext));

        //упаковка файлов в PDF
        dataContext.setPackageToPdf(draft.getPackageToPdf());

        return dataContext;
    }

    private Map<String, Object> retrieveParametersFromDraft(ScenarioDto draftBody) {
        draftBody.getApplicantAnswers().putAll(draftBody.getCurrentValue());
        TypeReference<Object> typeRef = new TypeReference<>() {};
        Map<String, Object> applicantAnswers = new HashMap<>();
        //TODO refactor
        draftBody.getApplicantAnswers().forEach(
                (key, val) -> {
                    String value = val.getValue();
                    if(!isNull(value)) {
                        if (value.contains("{") || value.contains("[")) {
                            try {
                                Object innerObject = mapper.readValue(value, typeRef);
                                applicantAnswers.put(key, innerObject);
                            } catch (IOException e) {
                                log.error("Cannot process inner value for application field with id {}, content of the field is: {}. Error: {}", key, value, e);
                                applicantAnswers.put(key, value);
                            }
                        } else {
                            applicantAnswers.put(key, value);
                        }
                    }
                }
        );
        return applicantAnswers;
    }

    private Map<String, Object> retrieveAdditionalParametersFromDraft(ScenarioDto draftBody, Long oid) {
        Map<String, Object> additionalValues = new HashMap<>(draftBody.getAdditionalParameters());
        if (!additionalValues.containsKey(SP_REQUEST_GUID)) {
            String requestGuid = UUID.randomUUID().toString();
            additionalValues.put(SP_REQUEST_GUID, requestGuid);
        }
        if (!additionalValues.containsKey(SP_REQUEST_HASH)) {
            String requestHash = UUID.randomUUID().toString();
            additionalValues.put(SP_REQUEST_HASH, requestHash);
        }

        // serviceInfo
        additionalValues.put(SERVICE_INFO_KEY, convertServiceInfoToMap(draftBody.getServiceInfo(), mapper));

        additionalValues.put(ROLE_INDEX_ATTR_NAME, draftBody.getParticipants().containsKey(oid.toString())?
                draftBody.getParticipants().get(oid.toString()).getIndex() :
                null);

        additionalValues.put(ROLE_COMPONENT_ATTR_NAME, draftBody.getParticipants().containsKey(oid.toString())?
                draftBody.getParticipants().get(oid.toString()).getComponent() :
                null);

        if(draftBody.getParticipants().containsKey(oid.toString()) && additionalValues.get(ROLE_COMPONENT_ATTR_NAME)!=null) {
            TypeReference<Object> typeRef = new TypeReference<>() {};
            log.info("Processing coapplicant application field with id: {}", additionalValues.get(ROLE_COMPONENT_ATTR_NAME));
            ApplicantAnswer coApplicantAppField = draftBody.getApplicantAnswers().get(additionalValues.get(ROLE_COMPONENT_ATTR_NAME));

            if(coApplicantAppField == null) {
                log.info("Not found coapplicant invoc component. Ignoring");
                return additionalValues;
            }
            String value = coApplicantAppField.getValue();
            if (value != null && (value.indexOf("{") != -1 || value.indexOf("[") != -1)) {
                try {
                    Object innerObject = mapper.readValue(value, typeRef);
                    if (innerObject instanceof ArrayList) {
                        List innerList = (ArrayList) innerObject;
                        Object applicantValues = innerList.get((Integer) additionalValues.get(ROLE_INDEX_ATTR_NAME));
                        if(applicantValues instanceof HashMap) {
                            additionalValues.putAll((HashMap)applicantValues);
                            return additionalValues;
                        }
                        log.warn("Inseparable coapplicant value. Ignoring");
                    }
                    if (innerObject instanceof LinkedHashMap) {
                        log.warn("Map coapplicant's application fields are not supported with id {}. Ignoring...", additionalValues.get(ROLE_COMPONENT_ATTR_NAME));
                    }
                } catch (IOException e) {
                    log.error("Cannot process inner value for application field", e);
                }
            } else {
                log.warn("Unsupported coaplicant value (not an array or object)");
            }
        }

        additionalValues.putAll(attachmentService.getAttachmentsDigestValues(draftBody, isDefaultDigestValueEnabled(draftBody)));

        return additionalValues;
    }

    /**
     * Включено ли дефолтное значение digestValue ждя чценария
     * @param draftBody Сценарий
     * @return включено true, иначе false
     */
    private boolean isDefaultDigestValueEnabled(ScenarioDto draftBody){

        var spDescriptionSection = getDescriptor(draftBody.getServiceDescriptorId());
        if (spDescriptionSection != null) {
            if (spDescriptionSection.getSpConfig() != null) {
                return spDescriptionSection.getSpConfig().isDefaultDigestValueEnabled();
            }
        }
        return true;
    }


    private Set<String> retrieveAttachmentParameters(ScenarioDto draft, TemplatesDataContext dataContext) {
        Set<String> attachements = draft.getAttachmentInfo().entrySet().stream()
                .filter(el -> draft.getApplicantAnswers().containsKey(el.getKey()) || el.getKey().equals("approval"))  // TODO: Данный фильтр исключает возможность передачи файлов, кроме как загруженных пользователем.
                .flatMap(e -> e.getValue().stream())
                .map(a -> a.getUploadMnemonic())
                .collect(Collectors.toSet());

        Set<String> cycledAnswersAttachments = new HashSet<>();
        draft.getAttachmentInfo().entrySet().stream().forEach(
                attach -> draft.getCycledApplicantAnswers().getAnswers().forEach(
                        answer -> answer.getItems().forEach(item-> {
                            if(item.getItemAnswers().containsKey(attach.getKey())) {
                                attach.getValue().stream().map(a -> a.getUploadMnemonic()).forEach(cycledAnswersAttachments::add);
                            }
                        })
                )
        );
        attachements.addAll(cycledAnswersAttachments);

        Set<String> additionalAttachments = new HashSet<>();
        draft.getAttachmentInfo().entrySet().stream()
                .filter(attachNode -> dataContext.getAdditionalValues().containsKey(attachNode.getKey()))
                .forEach(attachNode -> {
                    Map<String, Object> infoFromAdditionalParam = (Map<String, Object>) dataContext.getAdditionalValues().get(attachNode.getKey());
                    String additionalFileMnemonic = infoFromAdditionalParam.getOrDefault(ADITIONAL_PARAM_FILE_MNEMONIC_KEY, "").toString();
                    attachNode.getValue().stream()
                            .filter(el -> additionalFileMnemonic.isEmpty() || additionalFileMnemonic.equals(el.getUploadMnemonic()))
                            .map(AttachmentInfo::getUploadMnemonic)
                            .forEach(additionalAttachments::add);
                });
        attachements = additionalAttachments.isEmpty() ? attachements : additionalAttachments;
        return attachements;
    }

    //TODO double check this method + origin
    private Map convertServiceInfoToMap(ServiceInfoDto serviceInfo, ObjectMapper mapper) {
        Map map = null;
        try {
            map = mapper.readValue(mapper.writeValueAsBytes(serviceInfo), Map.class);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Ошибка при конвертации ServiceInfoDto в MAP", e);
            }
        }
        return map;
    }

    private SpDescriptionSection getDescriptor(String serviceId) {
        var descriptor = serviceDescriptorClient.getServiceDescriptor(serviceId);
        log.info("Получено описание сервиса {}", descriptor.getService());
        return mapper.convertValue(descriptor, SpDescriptionSection.class);
    }
}
