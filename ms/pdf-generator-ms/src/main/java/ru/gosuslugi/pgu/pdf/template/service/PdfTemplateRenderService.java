package ru.gosuslugi.pgu.pdf.template.service;

import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.pdf.template.model.data.TemplatesDataContext;

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
    byte[] createCommonPdfAttachment(TemplatesDataContext dataContext, FileDescription options);

    /**
     * Method for getting compiled json with report data
     * @param dataContext Данные для создаваемого файла
     * @param options Описание для создаваемого файла
     * @param ignoreErrors
     * @return
     */
    String getServicePdfJson(TemplatesDataContext dataContext, FileDescription options, Boolean ignoreErrors);

    /**
     * Optional additional pdf files (e.g. "Rosstat pdf files")
     * @param dataContext Данные для создаваемого файла
     * @param options Описание для создаваемого файла
     * @return
     */
    byte[] createPdfAttachnment(TemplatesDataContext dataContext, FileDescription options);
}