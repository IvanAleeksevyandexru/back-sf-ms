package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

/**
 * Сервис упаковки файлов в PDF
 */
public interface PdfPackageService {
    /**
     * Упаковка файлов в PDF
     * @param context context sp-adapter
     */
    void packageToPdf(TemplatesDataContext context);
}
