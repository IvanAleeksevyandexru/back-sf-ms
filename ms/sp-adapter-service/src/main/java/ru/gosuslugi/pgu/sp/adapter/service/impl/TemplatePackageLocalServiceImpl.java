package ru.gosuslugi.pgu.sp.adapter.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatePackageService;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Тестовый сервер локов для JUnit-тестов
 */
@Service
@ConditionalOnProperty(value = "service-descriptor-storage-client.integration", havingValue = "false")
public class TemplatePackageLocalServiceImpl implements TemplatePackageService {

    @Override
    public ReentrantReadWriteLock getLockForService(String serviceId) {
        return new ReentrantReadWriteLock();
    }
}
