package ru.gosuslugi.pgu.pdf.template.service.impl;

import static ru.gosuslugi.pgu.dto.pdf.data.AttachmentType.REQUEST;
import static ru.gosuslugi.pgu.pdf.template.util.FileNameUtil.determineTemplateFileName;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.HandlePdfAttachmentsRequestDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.dto.pdf.data.UniqueType;
import ru.gosuslugi.pgu.pdf.template.exception.InputDataValidationException;
import ru.gosuslugi.pgu.pdf.template.exception.PdfGenerateException;
import ru.gosuslugi.pgu.pdf.template.model.data.TemplatesDataContext;
import ru.gosuslugi.pgu.pdf.template.service.PdfGeneratorService;
import ru.gosuslugi.pgu.pdf.template.service.PdfTemplateRenderService;
import ru.gosuslugi.pgu.pdf.template.service.TemplatesDataContextService;
import ru.gosuslugi.pgu.pdf.template.util.FileNameUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Исходный вариант перенесен из SP-Adapter из класса SmevPdfServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    public static final String PDF_MIME_TYPE = "application/pdf";

    private final DraftClient draftClient;
    private final TemplatesDataContextService templatesDataContextService;
    private final AttachmentService attachmentService;
    private final PdfTemplateRenderService pdfTemplateRenderService;

    @Override
    public void handlePdfAttachments(HandlePdfAttachmentsRequestDto request) {
        TemplatesDataContext templatesDataContext = getTemplatesDataContext(request.getOrderId(), request.getOid(), request.getRoleId(), request.getOrgId(), null);
        createAndSavePdf(templatesDataContext, request.getOptions());
    }

    private void createAndSavePdf(TemplatesDataContext templatesDataContext, FileDescription options) {
        log.info("Trying to get pdf template to create");
        byte[] pdfContent = null;
        if(options.getType() == FileType.COMMON_PDF) {
            pdfContent = pdfTemplateRenderService.createCommonPdfAttachment(templatesDataContext, options);
        }
        if(options.getType() == FileType.PDF) {
            pdfContent = pdfTemplateRenderService.createPdfAttachnment(templatesDataContext, options);
        }
        saveAttachment(pdfContent, templatesDataContext, options);
    }

    /**
     * Сохранение файла PDF в хранилище Terrabyte
     * @param pdfContent содержимое PDF файла
     * @param templatesDataContext контекст шаблона
     * @param options описание файла
     */
    private void saveAttachment(byte[] pdfContent, TemplatesDataContext templatesDataContext, FileDescription options) {
        if (pdfContent != null) {
            Set<String> attachments = options.getAttachmentType() == REQUEST ? null : templatesDataContext.getAttachments();
            String fileName = FileNameUtil.buildAttachmentFileName(templatesDataContext, options);
            String mnemonic = FileNameUtil.buildAttachmentMnemonic(templatesDataContext, options);
            attachmentService.saveAttachment(templatesDataContext.getOrderId(), PDF_MIME_TYPE, fileName, mnemonic, pdfContent, attachments, templatesDataContext.getGeneratedFiles());
        }
    }

    /**
     * Не относится к отправке в СМЭВ
     */
    @Override
    public byte[] createApplicationPdf(Long orderId, Long oid, String pdfPrefix, String roleId, ScenarioDto scenarioDto) {
        TemplatesDataContext templatesDataContext = getTemplatesDataContext(orderId, oid, roleId, null, scenarioDto);
        //******** передать в параметрах
        FileDescription pdfFileDescription = new FileDescription();
        pdfFileDescription.setType(FileType.PDF);
        pdfFileDescription.setAddedFileName(UniqueType.NONE);
        String folderAdditionalPdf = templatesDataContext.getServiceId();
        Map<ApplicantRole, String> templatesPdfAdditional = Map.of(
                ApplicantRole.valueOf(roleId), determineTemplateFileName(folderAdditionalPdf, templatesDataContext.getServiceId(), ApplicantRole.valueOf(roleId), pdfPrefix)
        );
        pdfFileDescription.setTemplates(templatesPdfAdditional);
        //********
        return pdfTemplateRenderService.createCommonPdfAttachment(templatesDataContext, pdfFileDescription);
    }

    @Override
    public byte[] createAdditionalApplicationPdf(Long orderId, Long oid, String pdfPrefix, String roleId, ScenarioDto scenarioDto) {
        TemplatesDataContext templatesDataContext = getTemplatesDataContext(orderId, oid, roleId, null, scenarioDto);
        //******** передать в параметрах
        FileDescription pdfFileDescription = new FileDescription();
        pdfFileDescription.setType(FileType.PDF);
        pdfFileDescription.setAddedFileName(UniqueType.NONE);
        String folderAdditionalPdf = templatesDataContext.getServiceId() + "/additional";
        Map<ApplicantRole, String> templatesPdfAdditional = Map.of(
                ApplicantRole.valueOf(roleId), determineTemplateFileName(folderAdditionalPdf, templatesDataContext.getServiceId(), ApplicantRole.valueOf(roleId), pdfPrefix)
        );
        pdfFileDescription.setTemplates(templatesPdfAdditional);
        //********
        byte[] pdfContent = pdfTemplateRenderService.createPdfAttachnment(templatesDataContext, pdfFileDescription);

        if(pdfContent == null) {
            String errorInfo = "Cannot generate PDF file for draft with orderId "+ orderId + "for user with oid "+oid;
            log.error(errorInfo);
            throw new PdfGenerateException(errorInfo);
        }
        return pdfContent;
    }

    /**
     * Формирует контекст для vm-шаблона
     * @param orderId ИД черновика
     * @param oid ИД пользователя
     * @param roleId ИД роли пользователя
     * @return контекст для шаблона
     */
    private TemplatesDataContext getTemplatesDataContext(Long orderId, Long oid, String roleId, Long orgId, ScenarioDto scenarioDto) {
        if(Objects.nonNull(scenarioDto)){
            return templatesDataContextService.prepareRequestParameters(scenarioDto.getServiceCode(), orderId, oid, roleId, scenarioDto, orgId);
        }

        DraftHolderDto draft = draftClient.getDraftById(orderId, oid, null);
        if (draft == null) {
            String errorInfo = "Cannot retrieve draft with orderId " + orderId + " for user with oid " + oid;
            log.error(errorInfo);
            throw new InputDataValidationException(errorInfo);
        }
        return templatesDataContextService.prepareRequestParameters(draft.getBody().getServiceCode(), orderId, oid, roleId, draft.getBody(), orgId);
    }

}
