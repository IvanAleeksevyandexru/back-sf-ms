package ru.gosuslugi.pgu.sp.adapter.service;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface TemplatePackageService {

    ReentrantReadWriteLock getLockForService(String serviceId);
}
