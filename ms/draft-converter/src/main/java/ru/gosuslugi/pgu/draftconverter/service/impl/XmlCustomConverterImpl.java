package ru.gosuslugi.pgu.draftconverter.service.impl;

import ru.gosuslugi.pgu.common.rendering.render.data.RenderRequest;
import ru.gosuslugi.pgu.common.rendering.render.service.RenderService;
import ru.gosuslugi.pgu.draftconverter.context.service.TemplateDataService;
import ru.gosuslugi.pgu.draftconverter.data.ContextBuildingRequest;
import ru.gosuslugi.pgu.draftconverter.data.TemplateDataContext;
import ru.gosuslugi.pgu.draftconverter.service.DraftConverter;
import ru.gosuslugi.pgu.draftconverter.validation.service.ValidationService;
import ru.gosuslugi.pgu.dto.XmlCustomConvertRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Базовая реализация {@link DraftConverter} для обработки {@link XmlCustomConvertRequest}.
 */
@Service
@RequiredArgsConstructor
public class XmlCustomConverterImpl implements DraftConverter<XmlCustomConvertRequest, Map<Object, Object>> {
    private final TemplateDataService dataService;
    private final RenderService renderService;
    private final ValidationService validatingConverter;

    @Override
    public Map<Object, Object> convert(XmlCustomConvertRequest request) {
        ContextBuildingRequest contextBuildingRequest = prepareContextBuildingRequest(request);
        TemplateDataContext contextData = dataService.prepare(contextBuildingRequest);
        RenderRequest renderRequest = buildRenderRequest(contextData);
        final String rendered = renderService.render(renderRequest);
        return validatingConverter.validateJson(rendered);
    }

    @Override
    public ContextBuildingRequest prepareContextBuildingRequest(XmlCustomConvertRequest request) {
        return ContextBuildingRequest.builder()
                .serviceId(request.getServiceId())
                .xmlData(request.getXmlData())
                .jsonData(request.getJsonData())
                .fileName(String.format("%s/converter/%s_custom.vm", request.getServiceId(),
                        request.getTemplateName()))
                .build();
    }
}
