package ru.gosuslugi.pgu.draftconverter.context.service;

import ru.gosuslugi.pgu.draftconverter.data.ContextBuildingRequest;
import ru.gosuslugi.pgu.draftconverter.data.TemplateDataContext;

/**
 * Подготавливает данные для заполнения черновика.
 */
public interface TemplateDataService {

    /**
     * Формирует данные для заполнения черновика.
     *
     * @param request запрос на формирование данных.
     * @return данные для построения черновика.
     */
    TemplateDataContext prepare(ContextBuildingRequest request);
}
