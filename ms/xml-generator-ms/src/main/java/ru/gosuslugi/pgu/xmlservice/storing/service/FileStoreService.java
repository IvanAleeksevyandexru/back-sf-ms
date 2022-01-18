package ru.gosuslugi.pgu.xmlservice.storing.service;

import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.data.StoreResponse;

/**
 * Сохраняет содержимое файла XML в хранилище.
 */
public interface FileStoreService {
    /**
     * Сохраняет файл в хранилище.
     *
     * @param xmlContent содержимое файла.
     * @param templateContext контекст.
     * @return ответ с мнемоникой файла.
     */
    StoreResponse store(byte[] xmlContent, TemplateDataContext templateContext);
}
