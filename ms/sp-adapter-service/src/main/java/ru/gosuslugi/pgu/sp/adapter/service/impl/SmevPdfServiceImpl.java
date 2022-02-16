package ru.gosuslugi.pgu.sp.adapter.service.impl;

import static ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig.determineTemplateFileName;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.dto.pdf.data.UniqueType;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.exceptions.PdfGenerateException;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterInputDataException;
import ru.gosuslugi.pgu.sp.adapter.service.AbstractSmevFileService;
import ru.gosuslugi.pgu.sp.adapter.service.PdfTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.service.SmevPdfService;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatesDataContextService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Реализация замещается отдельным микросервисом pdf-generator-service.
 * Будет удалена после окончания тестирования/
 */
@Slf4j
@Deprecated
public class SmevPdfServiceImpl extends AbstractSmevFileService implements SmevPdfService {
    public static final String PDF_MIME_TYPE = "application/pdf";

    @Autowired
    private DraftClient draftClient;
    @Autowired
    private TemplatesDataContextService templatesDataContextService;

    @Autowired
    public void setAttachmentService(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    private AttachmentService attachmentService;
    private PdfTemplateRenderService pdfTemplateRenderService;

    @Autowired
    public void setPdfTemplateRenderService(PdfTemplateRenderService pdfTemplateRenderService) {
        this.pdfTemplateRenderService = pdfTemplateRenderService;
    }

    @Override
    public void handlePdfAttachments(TemplatesDataContext templatesDataContext, FileDescription options) {
        log.info("Trying to get pdf template to create");
        File pdfFile = null;
        if(options.getType() == FileType.COMMON_PDF) {
            pdfFile = pdfTemplateRenderService.createCommonPdfAttachment(templatesDataContext, options);
        }
        if(options.getType() == FileType.PDF) {
            pdfFile = pdfTemplateRenderService.createPdfAttachnment(templatesDataContext, options);
        }
        saveAttachment(pdfFile, templatesDataContext, options);
    }

    /**
     * Сохранение файла PDF в хранилище Terrabyte
     * @param pdfFile файл
     * @param templatesDataContext контекст шаблона
     * @param options описание файла
     */
    private void saveAttachment(File pdfFile, TemplatesDataContext templatesDataContext, FileDescription options) {
        if (pdfFile != null) {
            try {
                byte[] fileBody = Files.readAllBytes(pdfFile.toPath());
                String fileName = buildAttachmentFileName(templatesDataContext, options);
                String mnemonic = buildAttachmentMnemonic(templatesDataContext, options);
                Set<String> attachments =
                        isSendToSmevAllowed(options.getAttachmentType())
                                ? templatesDataContext.getAttachments()
                                : null;
                attachmentService.saveAttachment(templatesDataContext.getOrderId(), PDF_MIME_TYPE, fileName, mnemonic, fileBody, attachments, templatesDataContext.getGeneratedFiles());
            } catch (IOException e) {
                log.error("Error during processing pdf file for serviceId {} with orderId {}. Ignoring...", templatesDataContext.getServiceId(), templatesDataContext.getOrderId(), e);
            }
        }
    }

    /**
     * Не относится к отправке в СМЭВ
     * @param orderId
     * @param oid
     * @param pdfPrefix
     * @param roleId
     * @return
     */
    @Override
    public byte[] createApplicationPdf(Long orderId, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        TemplatesDataContext templatesDataContext = getTemplatesDataContext(orderId, oid, orgId, roleId, null, skip17Status);
        return this.createApplicationPdfFromTemplate(templatesDataContext,pdfPrefix,roleId, skip17Status);
    }

    @Override
    public byte[] createApplicationPdf(ScenarioDto order, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        TemplatesDataContext templatesDataContext = getTemplatesDataContext(null, oid, orgId, roleId, order, skip17Status);
        return this.createApplicationPdfFromTemplate(templatesDataContext,pdfPrefix,roleId, skip17Status);
    }

    private byte[] createApplicationPdfFromTemplate(TemplatesDataContext templatesDataContext,String pdfPrefix, String roleId, Boolean skip17Status){
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
        return readFileContent(pdfTemplateRenderService.createCommonPdfAttachment(templatesDataContext, pdfFileDescription));
    }

    @Override
    public byte[] createAdditionalApplicationPdf(ScenarioDto order, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        TemplatesDataContext templatesDataContext = getTemplatesDataContext(null, oid, orgId, roleId, order,skip17Status);
        File pdfFile = this.createAdditionalApplicationPdfFromContext(templatesDataContext,roleId,pdfPrefix, skip17Status);

        if(pdfFile == null) {
            String errorInfo = "Cannot generate PDF file for draft for custom order without OrderId for user with oid "+oid;
            log.error(errorInfo);
            throw new PdfGenerateException(errorInfo);
        }
        return readFileContent(pdfFile);
    }


    private File createAdditionalApplicationPdfFromContext(TemplatesDataContext templatesDataContext, String roleId, String pdfPrefix, Boolean skip17Status){
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
        return  pdfTemplateRenderService.createPdfAttachnment(templatesDataContext, pdfFileDescription);

    }


    @Override
    public byte[] createAdditionalApplicationPdf(Long orderId, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        TemplatesDataContext templatesDataContext = getTemplatesDataContext(orderId, oid, orgId, roleId, null, skip17Status);
        File pdfFile = this.createAdditionalApplicationPdfFromContext(templatesDataContext, roleId,pdfPrefix, skip17Status);

        if(pdfFile == null) {
            String errorInfo = "Cannot generate PDF file for draft with orderId "+ orderId + "for user with oid "+oid;
            log.error(errorInfo);
            throw new PdfGenerateException(errorInfo);
        }
        return readFileContent(pdfFile);
    }

    private byte[] readFileContent(File pdfFile) {
        try {
            return pdfFile == null ? null : Files.readAllBytes(pdfFile.toPath());
        } catch (IOException e) {
            throw new PdfGenerateException("Reading file exception: " + e.getMessage(), e);
        }
    }

    /**
     * Формирует контекст для vm-шаблона
     * @param orderId ИД черновика
     * @param oid ИД пользователя
     * @param roleId ИД роли пользователя
     * @return контекст для шаблона
     */
    private TemplatesDataContext getTemplatesDataContext(Long orderId, Long oid, Long orgId, String roleId, ScenarioDto order, Boolean skip17Status) {
        if(Objects.nonNull(order)){
            return templatesDataContextService.prepareRequestParameters(order.getServiceCode(), orderId, oid, roleId, order, null, skip17Status);
        }
        DraftHolderDto draft = draftClient.getDraftById(orderId, oid, orgId);

        if (draft == null) {
            String errorInfo = "Cannot retrieve draft with orderId " + orderId + "for user with oid " + oid;
            log.error(errorInfo);
            throw new SpAdapterInputDataException(errorInfo);
        }
        return templatesDataContextService.prepareRequestParameters(draft.getBody().getServiceCode(), orderId, oid, roleId, draft.getBody(), null, skip17Status);
    }
}
