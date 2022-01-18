package ru.gosuslugi.pgu.service.descriptor.storage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.ServiceDescriptorRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.DbServiceDescriptor;
import ru.gosuslugi.pgu.service.descriptor.storage.service.ServiceDescriptorService;
import ru.gosuslugi.pgu.service.descriptor.storage.validation.JsonValidationService;

import java.time.Instant;

@Slf4j
@Service
public class ServiceDescriptorServiceImpl implements ServiceDescriptorService {

    private static final Class<ServiceDescriptor> TARGET_CLASS = ServiceDescriptor.class;

    private final JsonValidationService validationService;
    private final ServiceDescriptorRepository repository;

    public ServiceDescriptorServiceImpl(JsonValidationService validationService, ServiceDescriptorRepository repository) {
        this.validationService = validationService;
        this.repository = repository;
    }

    @Override
    public String get(String serviceId) {
        return repository.findById(serviceId)
            .map(DbServiceDescriptor::getBody)
            .orElse("");
    }

    @Override
    public Instant save(String serviceId, String body) {
        validationService.validate(body, TARGET_CLASS);
        DbServiceDescriptor saved = repository.save(new DbServiceDescriptor(serviceId, Instant.now(), body));
        if (log.isInfoEnabled()) {
            log.info("New json structure was saved with serviceId: \"" + serviceId + "\", updated time: " + saved.getUpdated());
        }
        return saved.getUpdated();
    }
}
