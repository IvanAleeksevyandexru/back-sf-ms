package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.DevSpAdapterDto;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterInputDataException;
import ru.gosuslugi.pgu.sp.adapter.service.DevModeService;
import ru.gosuslugi.pgu.sp.adapter.service.PdfTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatesDataContextService;
import ru.gosuslugi.pgu.sp.adapter.service.XmlTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig.ADDITIONAL_APPLICATION_TEMPLATE_PREFIX;
import static ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig.APPROVAL_APPLICATION_TEMPLATE_PREFIX;

@Service
@ConditionalOnExpression("${dev-mode.enabled}")
@RequiredArgsConstructor
@Slf4j
public class DevModeServiceImpl implements DevModeService {

    private static final String DRAFT_BODY_ATTR_NAME = "body";
    private static final String PDF_TEMPLATE_PREFIX = "pdf";
    private static final String SCENARIO_DTO_ATTR_NAME = "scenarioDto";
    private final XmlTemplateRenderService xmlTemplateRenderService;
    private final PdfTemplateRenderService pdfTemplateRenderService;
    private final TemplatesDataContextService templatesDataContextService;
    private final DraftClient draftClient;
    private final ObjectMapper objectMapper;

    @Override
    public RenderTemplateResponse getServiceBusinessXml(DevSpAdapterDto requestDto) {
        RenderTemplateResponse result = new RenderTemplateResponse();
        TemplatesDataContext dataContext = null;
        try {
            dataContext = prepareTemplateDataContext(requestDto);
        } catch (SpAdapterInputDataException e) {
            result.setErrorInfo("Error");
            result.setResultData(e.getMessage());
            return result;
        }
        if (dataContext==null) {
            result.setErrorInfo("Error");
            result.setResultData("Cannot read draft from attribute scenarioDraftBody in json. This service accepts draft from draft-service or scenarioDtor from last step of form service");
            return result;
        }

        try {
            String xml = xmlTemplateRenderService.getXmlRequest(dataContext, DefaultOptionsSpConfig.getOptionBusinessXml(dataContext));
            result.setResultData(xml);
            result.setErrorInfo("Ok");
        } catch (Exception e) {
            result.setErrorInfo(e.getMessage());
            result.setResultData(xmlTemplateRenderService.getXmlRequest(dataContext, DefaultOptionsSpConfig.getOptionBusinessXml(dataContext), true));
        }
        return result;
    }

    public RenderTemplateResponse getServiceTransportXml(DevSpAdapterDto requestDto) {
        RenderTemplateResponse result = new RenderTemplateResponse();
        TemplatesDataContext dataContext = null;
        try {
            dataContext = prepareTemplateDataContext(requestDto);
        } catch (SpAdapterInputDataException e) {
            result.setErrorInfo("Error");
            result.setResultData(e.getMessage());
            return result;
        }
        if (dataContext==null) {
            result.setErrorInfo("Error");
            result.setResultData("Cannot read draft from attribute scenarioDraftBody in json. This service accepts draft from draft-service or scenarioDtor from last step of form service");
            return result;
        }

        try {
            String xml = xmlTemplateRenderService.getXmlRequest(dataContext, DefaultOptionsSpConfig.getOptionTransportXml(dataContext));
            result.setResultData(xml);
            result.setErrorInfo("Ok");
        } catch (Exception e) {
            result.setErrorInfo(e.getMessage());
            result.setResultData(xmlTemplateRenderService.getXmlRequest(dataContext, DefaultOptionsSpConfig.getOptionTransportXml(dataContext), true));
        }
        return result;
    }

    public RenderTemplateResponse getPdfRenderedJson(DevSpAdapterDto requestDto) {
        RenderTemplateResponse result = new RenderTemplateResponse();
        TemplatesDataContext dataContext = null;
        try {
            dataContext = prepareTemplateDataContext(requestDto);
        } catch (SpAdapterInputDataException e) {
            result.setErrorInfo("Error");
            result.setResultData(e.getMessage());
            return result;
        }
        if (dataContext==null) {
            result.setErrorInfo("Error");
            result.setResultData("Cannot read draft from attribute scenarioDraftBody in json. This service accepts draft from draft-service or scenarioDtor from last step of form service");
            return result;
        }

        try {
            String pdfJson = pdfTemplateRenderService.getServicePdfJson(dataContext, DefaultOptionsSpConfig.getOptionCommonPdf(dataContext), true);
            result.setResultData(pdfJson);
            result.setErrorInfo("Ok");
            if (pdfJson.contains("\\$")) {
                result.setErrorInfo("Placeholders found");
            }
        } catch (Exception e) {
            result.setErrorInfo(e.getMessage());
            result.setResultData(pdfTemplateRenderService.getServicePdfJson(dataContext, DefaultOptionsSpConfig.getOptionCommonPdf(dataContext), false));
        }

        return result;
    }

    public byte[] getServicePdf(DevSpAdapterDto requestDto) {
        TemplatesDataContext dataContext = null;
        dataContext = prepareTemplateDataContext(requestDto);

        if (dataContext==null) {
            throw new SpAdapterInputDataException("Cannot read draft from attribute scenarioDraftBody in json. This service accepts draft from draft-service or scenarioDtor from last step of form service");
        }

        File pdfFile = pdfTemplateRenderService.createCommonPdfAttachment(dataContext, DefaultOptionsSpConfig.getOptionCommonPdf(dataContext));
        if (pdfFile==null) {
            throw new SpAdapterInputDataException("PDF template is missing or invalid. Try to check json template using /jsonPdfTemplate");
        }
        try {
            return Files.readAllBytes(pdfFile.toPath());
        }catch (IOException e) {
            throw new SpAdapterInputDataException("Cannot read result pdf file. Backend error");
        }

    }

    public byte[] getServiceAdditionalPdf(DevSpAdapterDto requestDto, String type) {
        TemplatesDataContext dataContext = null;
        dataContext = prepareTemplateDataContext(requestDto);

        if (dataContext==null) {
            throw new SpAdapterInputDataException("Cannot read draft from attribute scenarioDraftBody in json. This service accepts draft from draft-service or scenarioDtor from last step of form service");
        }
        FileDescription fileDescription = null;
        switch(type) {
            case ADDITIONAL_APPLICATION_TEMPLATE_PREFIX: fileDescription = DefaultOptionsSpConfig.getOptionAdditionalPdf(dataContext); break;
            case APPROVAL_APPLICATION_TEMPLATE_PREFIX: fileDescription = DefaultOptionsSpConfig.getOptionApprovalPdf(dataContext); break;

        }
        File pdfFile = pdfTemplateRenderService.createPdfAttachnment(dataContext, fileDescription);
        if (pdfFile==null) {
            throw new SpAdapterInputDataException("PDF additional pdf template is missing");
        }
        try {
            return Files.readAllBytes(pdfFile.toPath());
        } catch (IOException e) {
            throw new SpAdapterInputDataException("Cannot read result pdf file. Backend error");
        }
    }

    private TemplatesDataContext prepareTemplateDataContext(DevSpAdapterDto requestDto) {
        LinkedHashMap dataFromRequest = requestDto.getScenarioDraftBody();
        if (dataFromRequest==null) {
            DraftHolderDto draft = draftClient.getDraftById(requestDto.getOrderId(), requestDto.getOid(), requestDto.getOrgId());
            if (draft == null) {
                log.error("Cannot retrieve draft and scenarioDraftBody attr is empty");
                throw new SpAdapterInputDataException("Cannot retrieve draft and scenarioDraftBody attr is empty");
            }
            return templatesDataContextService.prepareRequestParameters(requestDto.getServiceId(), requestDto.getOrderId(), requestDto.getOid(), requestDto.getRole(), draft.getBody(), requestDto.getOrgId(), false);
        }

        if (dataFromRequest.containsKey(DRAFT_BODY_ATTR_NAME) && dataFromRequest.get(DRAFT_BODY_ATTR_NAME)!=null) {
            LinkedHashMap<String, Object> body = (LinkedHashMap<String, Object>) dataFromRequest.get(DRAFT_BODY_ATTR_NAME);
            try {
                String draftString = objectMapper.writeValueAsString(body);
                ScenarioDto scenarioDto = objectMapper.readValue(draftString, ScenarioDto.class);
                scenarioDto.setAttachmentInfo(new HashMap<>());
                scenarioDto.setGeneratedFiles(new HashSet<>());
                return templatesDataContextService.prepareRequestParameters(requestDto.getServiceId(), requestDto.getOrderId(), requestDto.getOid(), requestDto.getRole(), scenarioDto, requestDto.getOrgId(), false);
            } catch (IOException e) {
                log.error("Cannot convert input data into Draft");
                throw new SpAdapterInputDataException("Cannot convert input data into Draft. It looks like Draft from draft service, but has wrong format");
            }
        }
        if (dataFromRequest.containsKey(SCENARIO_DTO_ATTR_NAME) && dataFromRequest.get(SCENARIO_DTO_ATTR_NAME)!=null) {
            LinkedHashMap<String, Object> scenarioDtoMap = (LinkedHashMap<String, Object>) dataFromRequest.get(SCENARIO_DTO_ATTR_NAME);
            try {
                String scenarioDtoString = objectMapper.writeValueAsString(scenarioDtoMap);
                ScenarioDto scenarioDto = objectMapper.readValue(scenarioDtoString, ScenarioDto.class);
                scenarioDto.setAttachmentInfo(new HashMap<>());
                scenarioDto.setGeneratedFiles(new HashSet<>());
                return templatesDataContextService.prepareRequestParameters(requestDto.getServiceId(), requestDto.getOrderId(), requestDto.getOid(), requestDto.getRole(), scenarioDto, requestDto.getOrgId(), false);
            } catch (IOException e) {
                log.error("Cannot convert input data into ScenarioDto");
                throw new SpAdapterInputDataException("Cannot convert input data into ScenarioDto. It looks like ScenarioDto from last step, but has wrong format");
            }
        }
        return null;
    }
}
