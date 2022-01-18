package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

/**
 * Service for handling all pdf attachments and saving it to terrabyte
 */
public interface SmevPdfService {

    void handlePdfAttachments(TemplatesDataContext templatesDataContext, FileDescription options);

    byte[] createAdditionalApplicationPdf(Long orderId, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status);

    byte[] createApplicationPdf(Long orderId, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status);

    byte[] createAdditionalApplicationPdf(ScenarioDto order, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status);

    byte[] createApplicationPdf(ScenarioDto order, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status);
}
