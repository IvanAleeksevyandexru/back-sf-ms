package ru.gosuslugi.pgu.service.descriptor.storage.service;

import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.TemplatePackage;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;

public interface TemplatePackageService {

    TemplatePackage get(String serviceId);

    Instant save(String serviceId, ByteBuffer body);

    TemplatePackage refresh(String serviceId);

    List<TemplatePackage> getServiceTemplateChecksums(List<String> serviceIds);
}
