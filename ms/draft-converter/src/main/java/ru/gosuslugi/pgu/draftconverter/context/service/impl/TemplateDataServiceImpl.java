package ru.gosuslugi.pgu.draftconverter.context.service.impl;

import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.draftconverter.context.service.ParseService;
import ru.gosuslugi.pgu.draftconverter.context.service.TemplateDataService;
import ru.gosuslugi.pgu.draftconverter.data.ContextBuildingRequest;
import ru.gosuslugi.pgu.draftconverter.data.TemplateDataContext;
import ru.gosuslugi.pgu.draftconverter.data.XmlElement;
import ru.gosuslugi.pgu.dto.SpDescriptionSection;
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient;

import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * {@inheritDoc}
 * <p>
 * В качестве источников данных используется XML из СМЭВ, JSON с прочими данными и информация об
 * услуге, запрашиваемая у service-descriptor-storage.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateDataServiceImpl implements TemplateDataService {
    private final ServiceDescriptorClient serviceDescriptorClient;
    private final ParseService<XmlElement> xmlParser;
    private final ParseService<Object> jsonParser;
    @Value("${service-descriptor-storage-client.integration:#{false}}")
    private boolean sdIntegrationEnabled;

    @Override
    public TemplateDataContext prepare(ContextBuildingRequest request) {
        TemplateDataContext dataContext = initFromRequest(request);
        if (request.getXmlData() != null) {
            final XmlElement xmlData = xmlParser.parse(request.getXmlData());
            dataContext.setXmlTree(xmlData);
        }
        final String jsonRequestData = request.getJsonData();
        if (Objects.nonNull(jsonRequestData) && !StringUtils.isEmpty(jsonRequestData)) {
            final Object jsonData = jsonParser.parse(jsonRequestData);
            dataContext.setJsonTree(jsonData);
        }
        populateFromServiceDescriptor(dataContext, request.getServiceId());
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            dataContext.getValues().putAll(request.getAnswers());
        }
        dataContext.setTemplateFileName(request.getFileName());

        return dataContext;
    }

    private void populateFromServiceDescriptor(final TemplateDataContext dataContext,
            final String serviceId) {
        if (!sdIntegrationEnabled) {
            return;
        }
        final SpDescriptionSection serviceDescription = getServiceDescriptor(serviceId);
        if (Objects.nonNull(serviceId)) {
            final Map<String, String> spDescriptionParameters = serviceDescription.getParameters();
            if (Objects.nonNull(spDescriptionParameters)) {
                dataContext.getServiceParameters().putAll(spDescriptionParameters);
            }
        }
    }

    private TemplateDataContext initFromRequest(final ContextBuildingRequest request) {
        TemplateDataContext dataContext = new TemplateDataContext();
        dataContext.setServiceId(request.getServiceId());
        return dataContext;
    }

    private SpDescriptionSection getServiceDescriptor(final String serviceId) {
        val descriptorString = serviceDescriptorClient.getServiceDescriptor(serviceId);
        log.info("Получено описание услуги с id {}", serviceId);
        return JsonProcessingUtil.fromJson(descriptorString, SpDescriptionSection.class);
    }
}
