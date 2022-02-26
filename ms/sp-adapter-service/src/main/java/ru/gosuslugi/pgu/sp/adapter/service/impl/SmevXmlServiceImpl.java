package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.pgu.PguClientService;
import ru.gosuslugi.pgu.sp.adapter.service.AbstractSmevFileService;
import ru.gosuslugi.pgu.sp.adapter.service.SmevXmlService;
import ru.gosuslugi.pgu.sp.adapter.service.XmlTemplateRenderService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmevXmlServiceImpl extends AbstractSmevFileService implements SmevXmlService {

    private final XmlTemplateRenderService xmlTemplateRenderService;

    private final AttachmentService attachmentService;

    private static final String XML_MIME_TYPE = "application/xml";

    private final PguClientService pguClientService;

    private final XmlValidationServiceImpl xmlValidationService;

    @Override
    public String getSmevRequest(TemplatesDataContext templatesDataContext, FileDescription fileDescription) {

        String xml = getSmevRequestOnly(templatesDataContext,fileDescription);

        if (fileDescription.getType() == FileType.REQUEST && !checkRequestXML(xml, templatesDataContext, fileDescription)) {
            return null;
        }

        xmlValidationService.validate(xml);

        if (fileDescription.getType() == FileType.REQUEST)
            logIllegalAttachmentFilename(templatesDataContext);

        if (!StringUtils.isEmpty(xml)) {
            attachmentService.saveAttachment(templatesDataContext.getOrderId(),
                    XML_MIME_TYPE,
                    buildAttachmentFileName(templatesDataContext, fileDescription),
                    buildAttachmentMnemonic(templatesDataContext, fileDescription),
                    xml.getBytes(),
                    isSendToSmevAllowed(fileDescription.getAttachmentType())
                            ? templatesDataContext.getAttachments()
                            : null,
                    templatesDataContext.getGeneratedFiles());
        }
        return xml;
    }

    private void logIllegalAttachmentFilename(TemplatesDataContext templatesDataContext) {
        val serviceId = templatesDataContext.getServiceId();
        if (Objects.equals(serviceId, "10000000104") && templatesDataContext.getBusinessXmlName() == null) {
            log.error("For serviceId {} property 'businessXmlName' is null.", serviceId);
        }
    }

    @Override
    public String getSmevRequestOnly(TemplatesDataContext templatesDataContext, FileDescription fileDescription) {
        boolean ignoreErrors = fileDescription.getType() != FileType.REQUEST;
        String xml = xmlTemplateRenderService.getXmlRequest(templatesDataContext, fileDescription, ignoreErrors);

        return xml;
    }

    private boolean checkRequestXML(String xml, TemplatesDataContext templatesDataContext, FileDescription fileDescription) {
        if (xml == null) {
            if (log.isErrorEnabled()) {
                log.error("Required xml template is missing {}", fileDescription);
            }
            return false;
        }
        if (xml.length() == 0) {
            if (log.isInfoEnabled()) {
                log.info("Empty business smev request was found for serviceId: {} orderId: {}. Ignoring message", templatesDataContext.getServiceId(), templatesDataContext.getOrderId());
                log.info("Deleting draft (current applicant do not send any SMEV request according business process)");
            }
            pguClientService.deleteOrderId(templatesDataContext.getOrderId());
            return false;
        }
        return true;
    }

}
