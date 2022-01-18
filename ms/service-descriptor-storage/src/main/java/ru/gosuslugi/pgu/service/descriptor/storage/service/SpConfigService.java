package ru.gosuslugi.pgu.service.descriptor.storage.service;

/**
 * Сервис для получения секции spConfig из общего конфига сервиса
 */
public interface SpConfigService {

    /**
     * Получение конфига SP из нужного дескриптора сервиса
     * @param serviceId ID сервиса
     * @return JSON часть spConfig
     */
    String get(String serviceId);

}
