package ru.gosuslugi.pgu.xmlservice.storing.service;

import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;

/**
 * Отвечает за присвоение имени и мнемоники для сохраняемого в хранилище файла.
 */
public interface FileNamingStrategy {
    /**
     * Вычисляет имя файла на основе dataContext.
     *
     * @param dataContext контекст.
     * @return имя файла.
     */
    String computeFileName(TemplateDataContext dataContext);

    /**
     * Вычисляет мнемонику файла на основе dataContext.
     *
     * @param dataContext контекст.
     * @return мнемоника файла.
     */
    String computeMnemonic(TemplateDataContext dataContext);
}
