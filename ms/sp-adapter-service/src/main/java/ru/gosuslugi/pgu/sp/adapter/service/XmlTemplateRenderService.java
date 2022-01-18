package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

/**
 * Service responsible for smev xml generation functions
 * Contains info about templates locations, proper escaping functions and passing data context
 */
public interface XmlTemplateRenderService {

    String getXmlRequest(TemplatesDataContext dataContext, FileDescription options);

    String getXmlRequest(TemplatesDataContext dataContext, FileDescription options, boolean ignoreErrors);

}
