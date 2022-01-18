package ru.gosuslugi.pgu.service.descriptor.storage.validation;

import ru.gosuslugi.pgu.service.descriptor.storage.exception.ServiceDescriptorValidationException;

public interface JsonValidationService {

    void validate(String body, Class<?> clazz) throws ServiceDescriptorValidationException;
}
