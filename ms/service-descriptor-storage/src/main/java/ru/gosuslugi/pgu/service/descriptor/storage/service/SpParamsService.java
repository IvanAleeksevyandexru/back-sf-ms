package ru.gosuslugi.pgu.service.descriptor.storage.service;

/**
 * Сервис для получения секции parameters из общего конфига сервиса
 */
public interface SpParamsService {

    /**
     * Получение секции parameters из нужного дескриптора сервиса
     * @param serviceId ID сервиса
     * @return JSON часть spConfig
     */
    String get(String serviceId);

}
