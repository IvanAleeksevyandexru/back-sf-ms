package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

/**
 * Service that has methods for handlign xml smev request
 * including deleting empty orderId, saving attachments etc
 */
public interface SmevXmlService {

    String getSmevRequestOnly(TemplatesDataContext templatesDataContext, FileDescription fileDescription);

    String getSmevRequest(TemplatesDataContext templatesDataContext, FileDescription fileDescription);
}
