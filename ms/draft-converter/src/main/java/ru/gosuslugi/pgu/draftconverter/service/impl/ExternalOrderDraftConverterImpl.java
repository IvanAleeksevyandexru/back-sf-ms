package ru.gosuslugi.pgu.draftconverter.service.impl;

import ru.gosuslugi.pgu.common.rendering.render.data.RenderRequest;
import ru.gosuslugi.pgu.common.rendering.render.service.RenderService;
import ru.gosuslugi.pgu.draftconverter.context.service.TemplateDataService;
import ru.gosuslugi.pgu.draftconverter.data.ContextBuildingRequest;
import ru.gosuslugi.pgu.draftconverter.data.TemplateDataContext;
import ru.gosuslugi.pgu.draftconverter.service.DraftConverter;
import ru.gosuslugi.pgu.draftconverter.validation.service.ValidationService;
import ru.gosuslugi.pgu.dto.ExternalOrderRequest;
import ru.gosuslugi.pgu.dto.ScenarioDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Базовая реализация {@link DraftConverter} для обработки {@link ExternalOrderRequest}.
 */
@Service
@RequiredArgsConstructor
public class ExternalOrderDraftConverterImpl implements DraftConverter<ExternalOrderRequest, ScenarioDto> {
    private final TemplateDataService dataService;
    private final RenderService renderService;
    private final ValidationService validatingConverter;

    @Override
    public ScenarioDto convert(ExternalOrderRequest request) {
        ContextBuildingRequest contextBuildingRequest = prepareContextBuildingRequest(request);
        TemplateDataContext contextData = dataService.prepare(contextBuildingRequest);
        RenderRequest renderRequest = buildRenderRequest(contextData);
        final String rendered = renderService.render(renderRequest);
        return validatingConverter.validate(rendered);
    }

    @Override
    public ContextBuildingRequest prepareContextBuildingRequest(ExternalOrderRequest request) {
        return ContextBuildingRequest.builder()
                .serviceId(request.getServiceId())
                .answers(request.getAnswers())
                .fileName(String.format("%s/draft_from_external_request_%s.vm",
                        request.getServiceId(), request.getServiceId()))
                .build();
    }
}
