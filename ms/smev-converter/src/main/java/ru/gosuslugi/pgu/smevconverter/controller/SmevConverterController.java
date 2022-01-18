package ru.gosuslugi.pgu.smevconverter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.SmevConverterRequestDto;
import ru.gosuslugi.pgu.smevconverter.service.SmevConverterService;

import javax.validation.Valid;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/services", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class SmevConverterController {

    private final SmevConverterService smevConverterService;

    @PostMapping("/get")
    @Operation(summary = "Конвертирует XML в произвольный json", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public Map<Object, Object> get(@Parameter(description = "Запрос на получение json", required = true) @Valid @RequestBody SmevConverterRequestDto request) {
        return smevConverterService.get(request);
    }
}
