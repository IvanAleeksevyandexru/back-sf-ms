package ru.gosuslugi.pgu.generator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest;
import ru.gosuslugi.pgu.generator.service.fines.AppealFinesService;

import javax.validation.Valid;

/**
 * Controller that handle all methods for scenarios
 * Next/prev pages
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/scenario/fines", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AppealFinesController {

    private final AppealFinesService appealFinesService;

    @PostMapping
    @Operation(summary = "Генерация сценария", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public void generateScenario(@RequestBody @Valid AppealFinesRequest request) {
        log.info("Generation started for request {}", request);

        appealFinesService.generateMainService(request);
    }

    @PostMapping("/debug")
    @Operation(summary = "Генерация сценария, отладка", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка",
                    content = @Content(schema = @Schema(implementation = ServiceDescriptor.class)))
    })
    public ServiceDescriptor generateScenarioWithDebug(@RequestBody @Valid AppealFinesRequest request) {
        log.info("DEBUG Generation started for request {}", request);

        return appealFinesService.generateMainService(request);
    }

    @PostMapping("/additional")
    @Operation(summary = "Дополнительные шаги", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public void additionalSteps(@Parameter(name = "serviceId", in = ParameterIn.QUERY, description = "ID сервиса", schema = @Schema(type = "string"))
                                    @RequestParam String serviceId) {
        log.info("Started generation of additional step for Appeal Fines. ServiceId: {}", serviceId);

        appealFinesService.generateAdditionalSteps(serviceId);
    }

    @PostMapping("/additional/debug")
    @Operation(summary = "Дополнительные шаги, отладка", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка",
                    content = @Content(schema = @Schema(implementation = ServiceDescriptor.class)))
    })
    public ServiceDescriptor additionalStepsWithDebug(@Parameter(name = "serviceId", in = ParameterIn.QUERY, description = "ID сервиса", schema = @Schema(type = "string"))
                                                          @RequestParam String serviceId) {
        log.info("DEBUG Started generation of additional step for Appeal Fines. ServiceId: {}", serviceId);

        return appealFinesService.generateAdditionalSteps(serviceId);
    }

}
