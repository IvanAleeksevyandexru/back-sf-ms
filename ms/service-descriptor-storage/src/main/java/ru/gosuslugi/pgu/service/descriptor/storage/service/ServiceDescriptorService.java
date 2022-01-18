package ru.gosuslugi.pgu.service.descriptor.storage.service;

import java.time.Instant;

public interface ServiceDescriptorService {

    String get(String serviceId);

    Instant save(String serviceId, String body);
}
