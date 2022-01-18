package ru.gosuslugi.pgu.pdf.template.service;

import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.HandlePdfAttachmentsRequestDto;

/**
 * Исходный вариант перенесен из SP-Adapter из класса SmevPdfServiceImpl

 * Service for handling all pdf attachments and saving it to terrabyte
 */
public interface PdfGeneratorService {

    byte[] createAdditionalApplicationPdf(Long orderId, Long oid, String pdfPrefix, String roleId, ScenarioDto order);

    byte[] createApplicationPdf(Long orderId, Long oid, String pdfPrefix, String roleId, ScenarioDto order);

    void handlePdfAttachments(HandlePdfAttachmentsRequestDto request);

}
