package ru.gosuslugi.pgu.service.descriptor.storage.service;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.core.exception.PguException;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.service.descriptor.storage.config.props.ComponentRegistryProperties;
import ru.gosuslugi.pgu.service.descriptor.storage.model.stats.Component;
import ru.gosuslugi.pgu.service.descriptor.storage.model.stats.ServiceInfo;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.ServiceDescriptorRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.DbServiceDescriptor;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.ServiceDescriptorProjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentRegistryCachedService {

    private final ServiceDescriptorRepository serviceDescriptorRepository;
    private final CassandraTemplate cassandraTemplate;
    private final ComponentRegistryProperties properties;

    @Cacheable(cacheNames = {"cacheComponentRegistry"}, sync = true)
    public List<ServiceInfo> getCachedStatistic() {
        log.info("Start loading statistic from database");

        List<String> serviceIdList = findServiceIds();
        log.info("Found serviceIds: {}", serviceIdList);

        return serviceIdList.stream()
                .parallel()
                .map(this::loadService)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private ServiceInfo loadService(String serviceId) {
        try {
            DbServiceDescriptor dbDescriptor = serviceDescriptorRepository.findById(serviceId)
                    .orElseThrow(() -> new PguException("ServiceId found, but descriptor not founs for " + serviceId));
            ServiceDescriptorProjection serviceDescriptor = JsonProcessingUtil.fromJson(dbDescriptor.getBody(), ServiceDescriptorProjection.class);
            if (serviceDescriptor.getApplicationFields() == null) {
                return null;
            }
            Map<String, Component> componentMap = new HashMap<>();

            serviceDescriptor.getApplicationFields().forEach(fieldComponent -> {
                if (fieldComponent.getType() == null) {
                    return;
                }
                Component component = componentMap.computeIfAbsent(
                        fieldComponent.getType(),
                        key -> new Component(fieldComponent.getType(), 0));
                component.setAmount(component.getAmount() + 1);
            });
            List<Component> components = new ArrayList<>(componentMap.values());
            components.sort(comparing(Component::getComponentType));
            return new ServiceInfo(dbDescriptor.getServiceId(), components);
        } catch (Exception e) {
            log.error("Error on processing service {}", serviceId, e);
            return null;
        }
    }

    private List<String> findServiceIds() {
        SimpleStatement statement = SimpleStatement.newInstance("select serviceid from service_descriptor")
                .setTimeout(properties.getFindQueryTimeout());
        return cassandraTemplate.getCqlOperations().queryForList(statement, String.class).stream()
                .filter(s -> !s.startsWith("10000000305_")) // Для услуги "Обжалование штрафов" генерятся дескрипторы вида "10000000305_<orderId>", их нужно игнорировать
                .sorted()
                .collect(toList());
    }

}
