package ru.gosuslugi.pgu.draftconverter.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.draftconverter.service.DraftConverter;
import ru.gosuslugi.pgu.dto.ExternalOrderRequest;
import ru.gosuslugi.pgu.dto.ScenarioDto;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Производит конвертацию XML из СМЭВ в черновик.
 */
@RestController
@RequestMapping(path = "/services", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ExternalRequestDraftConverterController {

    private final DraftConverter<ExternalOrderRequest, ScenarioDto> converter;

    @PostMapping(value = "/convert/externalOrder")
    @Operation(summary = "Конвертирует ExternalOrderRequest в DTO черновика")
    public ScenarioDto convert(
            @Parameter(description = "Параметры запроса", required = true)
            @RequestBody ExternalOrderRequest request) {
        return converter.convert(request);
    }
}
