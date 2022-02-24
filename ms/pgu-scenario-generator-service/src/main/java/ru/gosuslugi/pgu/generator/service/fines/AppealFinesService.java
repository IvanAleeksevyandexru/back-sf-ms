package ru.gosuslugi.pgu.generator.service.fines;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.logging.annotation.Log;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.generator.client.IpshRestClient;
import ru.gosuslugi.pgu.generator.exception.DescriptorGenerationException;
import ru.gosuslugi.pgu.generator.model.appeal.cycle.AppealCycleResponse;
import ru.gosuslugi.pgu.generator.model.appeal.scenario.GetAppealScenarioResponse;
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest;
import ru.gosuslugi.pgu.generator.service.FileStorageService;
import ru.gosuslugi.pgu.generator.service.XmlUnmarshallService;
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import static ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil.*;

@Log
@Service
@RequiredArgsConstructor
public class AppealFinesService {

    private static final String APPEAL_FINES_SERVICE_ID = "10000000305";
    private static final DateTimeFormatter DATE_FORMAT =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final AppealDescriptorGenerator appealDescriptorGenerator;
    private final AdditionalStepsAppealDescriptorGenerator additionalStepsGenerator;
    private final XmlUnmarshallService xmlUnmarshallService;
    private final ServiceDescriptorClient serviceDescriptorClient;
    private final FileStorageService fileStorageService;
    private final IpshRestClient ipshRestClient;

    public ServiceDescriptor generateMainService(AppealFinesRequest request) {
        GetAppealScenarioResponse xmlResponse = ipshRestClient.getFinesAppealXml(request);

        ServiceDescriptor initialDescriptor = loadFromStorage(APPEAL_FINES_SERVICE_ID);
        saveParametersToDescriptor(initialDescriptor, request, xmlResponse);

        ServiceDescriptor generatedServiceDescriptor =
                appealDescriptorGenerator.generateMainScenario(initialDescriptor, xmlResponse);

        serviceDescriptorClient.saveServiceDescriptor(request.getServiceId(), generatedServiceDescriptor);

        return generatedServiceDescriptor;
    }

    public ServiceDescriptor generateAdditionalSteps(String serviceId) {

        ServiceDescriptor serviceDescriptor = loadFromStorage(serviceId);

        AppealFinesRequest request = restoreParametersFromDescriptor(serviceId, serviceDescriptor);

        String responseXml = fileStorageService.loadXmlByOrderId(parseOrderId(serviceId));
        AppealCycleResponse additionalDocsRequest = xmlUnmarshallService.unmarshal(responseXml, AppealCycleResponse.class);

        ServiceDescriptor updatedDescriptor = additionalStepsGenerator.addAdditionalStepsToService(serviceDescriptor, additionalDocsRequest, request);

        serviceDescriptorClient.saveServiceDescriptor(serviceId, updatedDescriptor);

        return serviceDescriptor;
    }

    private ServiceDescriptor loadFromStorage(String serviceId) {
        ServiceDescriptor serviceDescriptor = serviceDescriptorClient.getServiceDescriptor(serviceId);
        if (serviceDescriptor == null) {
            throw new DescriptorGenerationException("Service Descriptor not found for service " + serviceId);
        }
        return serviceDescriptorDeepCopy(serviceDescriptor);
    }

    private ServiceDescriptor serviceDescriptorDeepCopy(ServiceDescriptor serviceDescriptor) {
        return fromJson(toJson(serviceDescriptor), ServiceDescriptor.class);
    }

    private String parseOrderId(String serviceId) {
        return serviceId.split("_")[1];
    }

    public void saveParametersToDescriptor(ServiceDescriptor descriptor, AppealFinesRequest request, GetAppealScenarioResponse response) {
        if (Objects.isNull(descriptor.getParameters())) {
            descriptor.setParameters(new HashMap<>());
        }
        descriptor.getParameters().put("Id", response.getId());
        descriptor.getParameters().put("ReqId", response.getReqId());
        Calendar timestamp = response.getTimestamp();
        descriptor.getParameters().put("timestamp", timestamp.toInstant().atZone(timestamp.getTimeZone().toZoneId()).toOffsetDateTime().format(DATE_FORMAT));
        descriptor.getParameters().put("uin", request.getBillNumber());
        descriptor.getParameters().put("routeNumber", request.getRouteNumber());
        descriptor.getParameters().put("billDate", request.getBillDate());
    }

    public AppealFinesRequest restoreParametersFromDescriptor(String serviceId, ServiceDescriptor serviceDescriptor) {
        AppealFinesRequest request = new AppealFinesRequest();

        request.setServiceId(serviceId);
        request.setBillNumber(serviceDescriptor.getParameters().get("uin"));
        request.setRouteNumber(serviceDescriptor.getParameters().get("routeNumber"));
        request.setBillDate(serviceDescriptor.getParameters().get("billDate"));

        return request;
    }

}
