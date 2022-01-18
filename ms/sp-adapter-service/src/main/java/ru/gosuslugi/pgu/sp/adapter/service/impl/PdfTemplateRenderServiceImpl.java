package ru.gosuslugi.pgu.sp.adapter.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.dto.pdf.RenderRequestDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.pdf.PdfGenerator;
import ru.gosuslugi.pgu.sp.adapter.service.AbstractTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.service.PdfTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.service.RenderService;
import ru.gosuslugi.pgu.sp.adapter.types.EscaperType;
import ru.gosuslugi.pgu.sp.adapter.types.PdfFileField;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfTemplateRenderServiceImpl extends AbstractTemplateRenderService implements PdfTemplateRenderService {

    private final RenderService renderService;
    private final PdfGenerator pdfGenerator;
    private final ObjectMapper mapper;

    @Override
    public File createCommonPdfAttachment(TemplatesDataContext dataContext, FileDescription options) {
        String pdfRenderedData = getServicePdfJson(dataContext, options, true);

        PdfFileField[] pdfFields;

        if(pdfRenderedData == null) {
            return null;
        }
        try {
            pdfFields = mapper.readValue(pdfRenderedData, PdfFileField[].class);
        } catch (JsonProcessingException e) {
            String templateName = options.getTemplates().get(dataContext.getRoleId());
            String errorMessage = String.format("Произошла ошибка во время разбора полей PDF-файла. Возможно, шаблон (%s) составлен некорректно. Доп. информация: %s", templateName, e.getMessage());
            log.error(errorMessage, e);
            // Не делаем rethrow, т.к. pdf не являются критичными. См. EPGUCORE-53254
            return null;
        }
        Map<String, String> additionalParams = new HashMap<>();
        if(Objects.nonNull(dataContext.getOrderId())){
            additionalParams.put("orderId", Long.toString(dataContext.getOrderId()));
        }
        additionalParams.put("serviceId", dataContext.getServiceId());
        return pdfGenerator.createPdfFile(Arrays.asList(pdfFields), additionalParams);
    }

    @Override
    public File createPdfAttachnment(TemplatesDataContext dataContext, FileDescription options) {

        String templateFileName = options.getTemplates().get(dataContext.getRoleId());
        RenderRequestDto renderRequest = getRenderRequest(dataContext, templateFileName, false, EscaperType.PDF_ADD);

        String additionalPdfContent = renderService.executeTemplate(renderRequest, true);
        if (additionalPdfContent == null || additionalPdfContent.length() == 0) {
            log.info("Scenario does not support additional pdf generation with {} type for service {} and role {}", options.getType(), dataContext.getServiceId(), dataContext.getRoleId());
            return null;
        }
        return pdfGenerator.createVelocityPdfFile(additionalPdfContent);
    }

    @Override
    public String getServicePdfJson(TemplatesDataContext dataContext, FileDescription options, boolean ignoreErrors) {
        String templateFileName = options.getTemplates().get(dataContext.getRoleId());
        RenderRequestDto renderRequest = getRenderRequest(dataContext, templateFileName, false, EscaperType.PDF);
        return renderService.executeTemplate(renderRequest, ignoreErrors);
    }
}
