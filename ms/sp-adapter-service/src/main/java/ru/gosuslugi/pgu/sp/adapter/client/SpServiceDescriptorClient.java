package ru.gosuslugi.pgu.sp.adapter.client;

import ru.gosuslugi.pgu.dto.Descriptor;

import java.util.Map;

/**
 * Клиент дескриптора сервисов
 */
public interface SpServiceDescriptorClient {

    /**
     * Получение секции parameters из конфига сервиса
     * @param serviceId id сервиса
     * @return секция parameters или null в случае её отсутствия
     */
    Map<String, String> getSpParams(String serviceId);

    /**
     * Получение секции spConfig из конфига сервиса
     * @param serviceId id сервиса
     * @return секция spConfig или null в случае её отсутствия
     */
    Descriptor getSpConfig(String serviceId);

}
