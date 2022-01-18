package ru.gosuslugi.pgu.draftconverter.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.draftconverter.service.DraftConverter;
import ru.gosuslugi.pgu.dto.XmlCustomConvertRequest;

import javax.validation.Valid;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/services", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class XmlCustomConverterController {

    private final DraftConverter<XmlCustomConvertRequest, Map<Object, Object>> converter;

    @PostMapping(value = "/convert/custom")
    @Operation(summary = "Конвертирует xml в формат json")
    public Map<Object, Object> convert(@Parameter(description = "Конвертируемые данные", required = true)
                                 @Valid @RequestBody XmlCustomConvertRequest request) {
        return converter.convert(request);
    }
}