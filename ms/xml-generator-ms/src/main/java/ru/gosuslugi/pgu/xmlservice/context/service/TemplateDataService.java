package ru.gosuslugi.pgu.xmlservice.context.service;

import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;

/**
 * Формирует данные для построения XML-файла.
 */
public interface TemplateDataService {

    /**
     * Формирует данные для построения XML-файла из черновика.
     *
     * @param request запрос на формирование данных.
     * @return данные для построения XML-файла из черновика.
     */
    TemplateDataContext prepare(GenerateXmlRequest request);
}
