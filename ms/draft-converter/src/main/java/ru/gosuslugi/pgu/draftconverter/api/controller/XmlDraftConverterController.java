package ru.gosuslugi.pgu.draftconverter.api.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import ru.gosuslugi.pgu.draftconverter.service.DraftConverter;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.XmlDraftConvertRequest;

import javax.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Производит конвертацию XML из СМЭВ в черновик.
 */
@RestController
@RequestMapping(path = "/services", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class XmlDraftConverterController {

    private final DraftConverter<XmlDraftConvertRequest, ScenarioDto> converter;

    @PostMapping(value = "/{serviceId}/convert")
    @Operation(summary = "Конвертирует XML в DTO черновика")
    public ScenarioDto convert(
            @Parameter(description = "Код услуги", required = true) @PathVariable String serviceId,
            @Parameter(description = "Параметры запроса", required = true)
            @Valid @RequestBody XmlDraftConvertRequest request) {
        request.setServiceId(serviceId);
        return converter.convert(request);
    }
}
