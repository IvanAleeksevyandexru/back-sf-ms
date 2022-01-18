package ru.gosuslugi.pgu.pdf.template.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;
import ru.gosuslugi.pgu.pdf.template.exception.CriticalPdfGenerationException;
import ru.gosuslugi.pgu.pdf.template.exception.PdfGenerateException;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.pdf.template.model.data.TemplatesDataContext;
import ru.gosuslugi.pgu.pdf.template.model.types.EscaperType;
import ru.gosuslugi.pgu.pdf.template.model.types.PdfFileField;
import ru.gosuslugi.pgu.pdf.template.pdf.PdfGenerator;
import ru.gosuslugi.pgu.pdf.template.service.AbstractTemplateRenderService;
import ru.gosuslugi.pgu.pdf.template.service.PdfTemplateRenderService;
import ru.gosuslugi.pgu.pdf.template.render.RenderServiceWrapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfTemplateRenderServiceImpl extends AbstractTemplateRenderService implements PdfTemplateRenderService {

    private final RenderServiceWrapper renderService;
    private final PdfGenerator pdfGenerator;
    private final ObjectMapper mapper;

    @Override
    public byte[] createCommonPdfAttachment(TemplatesDataContext dataContext, FileDescription options) {
        String pdfRenderedData = getServicePdfJson(dataContext, options, false);

        PdfFileField[] pdfFields;

        if(pdfRenderedData == null) {
            return null;
        }
        try {
            pdfFields = mapper.readValue(pdfRenderedData, PdfFileField[].class);
        } catch (JsonProcessingException e) {
            String templateName = options.getTemplates().get(dataContext.getRoleId());
            String errorMessage = String.format("Произошла ошибка во время разбора полей PDF-файла. Возможно, шаблон (%s) составлен некорректно. Доп. информация: %s", templateName, e.getMessage());
            throw new PdfGenerateException(errorMessage, e);
        }
        Map<String, String> additionalParams = new HashMap<>();
        additionalParams.put("orderId", Long.toString(dataContext.getOrderId()));
        additionalParams.put("serviceId", dataContext.getServiceId());
        return pdfGenerator.generatePdfContent(Arrays.asList(pdfFields), additionalParams);
    }

    @Override
    public byte[] createPdfAttachnment(TemplatesDataContext dataContext, FileDescription options) {

        String templateFileName = options.getTemplates().get(dataContext.getRoleId());
        RenderRequestDto renderRequest = getRenderRequest(dataContext, templateFileName, false, EscaperType.PDF_ADD);

        RenderTemplateResponse response = renderService.render(renderRequest);
        if (!StringUtils.isEmpty(response.getErrorInfo())) {
            log.error("Additional pdf render error: " + response.getErrorInfo());
        }
        String additionalPdfContent = response.getResultData();

        if (additionalPdfContent == null || additionalPdfContent.length() == 0) {
            log.info("Scenario does not support additional pdf generation with {} type for service {} and role {}", options.getType(), dataContext.getServiceId(), dataContext.getRoleId());
            return null;
        }
        return pdfGenerator.createVelocityPdfFile(additionalPdfContent);
    }

    @Override
    public String getServicePdfJson(TemplatesDataContext dataContext, FileDescription options, Boolean ignoreErrors) {
        String templateFileName = options.getTemplates().get(dataContext.getRoleId());
        RenderRequestDto renderRequest = getRenderRequest(dataContext, templateFileName, false, EscaperType.PDF);
        RenderTemplateResponse response = renderService.render(renderRequest);
        if (ignoreErrors && !StringUtils.isEmpty(response.getErrorInfo())) {
            throw new CriticalPdfGenerationException(response.getErrorInfo());
        }
        return (response != null) ? response.getResultData() : null;
    }
}
