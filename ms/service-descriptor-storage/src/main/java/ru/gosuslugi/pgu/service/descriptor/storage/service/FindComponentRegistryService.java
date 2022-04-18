package ru.gosuslugi.pgu.service.descriptor.storage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.gosuslugi.pgu.service.descriptor.storage.controller.model.ScenarioStatsRequest;
import ru.gosuslugi.pgu.service.descriptor.storage.controller.model.ScenarioStatsResponse;
import ru.gosuslugi.pgu.service.descriptor.storage.model.stats.Component;
import ru.gosuslugi.pgu.service.descriptor.storage.model.stats.ServiceInfo;

import java.util.List;
import java.util.stream.Collectors;

import static ru.gosuslugi.pgu.service.descriptor.storage.controller.model.ScenarioStatsRequest.FilterType.componentType;

@Service
@RequiredArgsConstructor
public class FindComponentRegistryService {

    private final ComponentRegistryCachedService componentRegistryCachedService;

    public ScenarioStatsResponse findStatistic(ScenarioStatsRequest request) {

        List<ServiceInfo> services = findServices(request);

        return new ScenarioStatsResponse(services, services.size(), calcTotalComponents(services));
    }

    private List<ServiceInfo> findServices(ScenarioStatsRequest request) {
        if (CollectionUtils.isEmpty(request.getFilters().getItems())) {
            return componentRegistryCachedService.getCachedStatistic();
        }
        if (request.getFilters().getType() == componentType) {
            return findByComponentTypes(request.getFilters().getItems());
        }
        return findByServiceId(request.getFilters().getItems());
    }

    private List<ServiceInfo> findByComponentTypes(List<String> items) {
        return componentRegistryCachedService.getCachedStatistic().stream()
                .filter(service -> hasComponents(service, items))
                .map(service -> new ServiceInfo(service.getServiceId(), filterComponents(service.getComponents(), items)))
                .collect(Collectors.toList());
    }

    private List<ServiceInfo> findByServiceId(List<String> items) {
        return componentRegistryCachedService.getCachedStatistic().stream()
                .filter(service -> items.contains(service.getServiceId()))
                .collect(Collectors.toList());
    }

    private int calcTotalComponents(List<ServiceInfo> services) {
        return services.stream()
                .flatMap(service -> service.getComponents().stream())
                .mapToInt(Component::getAmount)
                .sum();
    }

    private List<Component> filterComponents(List<Component> components, List<String> items) {
        return components.stream()
                .filter(component -> items.contains(component.getComponentType()))
                .collect(Collectors.toList());
    }

    private boolean hasComponents(ServiceInfo service, List<String> items) {
        return !filterComponents(service.getComponents(), items).isEmpty();
    }


}
