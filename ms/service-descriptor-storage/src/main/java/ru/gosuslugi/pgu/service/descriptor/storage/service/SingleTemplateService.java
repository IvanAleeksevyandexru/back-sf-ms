package ru.gosuslugi.pgu.service.descriptor.storage.service;

import java.io.IOException;
import java.nio.ByteBuffer;

/*
Сервис для получения VM шаблона из архива с шаблонами, привязанного к бизнес процессу
 */
public interface SingleTemplateService {

    /**
     * Получение файла шаблона
     * @param serviceId идентификатор сервиса
     * @param path путь к шаблону
     * @return байты файла
     * @throws IOException что-то пошло не так
     */
    ByteBuffer get(String serviceId, String path) throws IOException;

    /**
     * Получение CRC файла шаблона
     * @param serviceId идентификатор сервиса
     * @param path путь к шаблону
     * @return CRC
     * @throws IOException что-то пошло не так
     */
    Long getCRC(String serviceId, String path) throws IOException;

}
