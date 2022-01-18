package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

import java.io.File;

/**
 * Service responsible for pdf files generation functions
 * Contains info about templates locations, proper escaping functions and passing data context
 */
public interface PdfTemplateRenderService {

    /**
     * Main service pdf-file
     * @param dataContext Данные для создаваемого файла
     * @param options Описание для создаваемого файла
     * @return
     */
    File createCommonPdfAttachment(TemplatesDataContext dataContext, FileDescription options);

    /**
     * Method for getting compiled json with report data
     * @param dataContext Данные для создаваемого файла
     * @param options Описание для создаваемого файла
     * @param ignoreErrors
     * @return
     */
    String getServicePdfJson(TemplatesDataContext dataContext, FileDescription options, boolean ignoreErrors);

    /**
     * Optional additional pdf files (e.g. "Rosstat pdf files")
     * @param dataContext Данные для создаваемого файла
     * @param options Описание для создаваемого файла
     * @return
     */
    File createPdfAttachnment(TemplatesDataContext dataContext, FileDescription options);
}
