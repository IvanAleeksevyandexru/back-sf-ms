package ru.gosuslugi.pgu.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.gosuslugi.pgu.service.holder.FrontendConfigHolder;

@RestController
@RequestMapping("/config/{serviceId}")
@RequiredArgsConstructor
public class ConfigController {

    @Value("${acceptFromPost}")
    private Boolean acceptFromPost;

    private final FrontendConfigHolder frontendConfigHolder;

    @GetMapping
    @Operation(summary = "Получение конфига", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public Object getConfig(@Parameter(name = "serviceId", in = ParameterIn.PATH, description = "ID сервиса", schema = @Schema(type = "string"))
                                @PathVariable String serviceId){
        return frontendConfigHolder.getConfigByServiceId(serviceId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @Operation(summary = "Получение конфига", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public ResponseEntity setConfig(@Parameter(name = "serviceId", in = ParameterIn.PATH, description = "ID сервиса", schema = @Schema(type = "string"))
                                    @PathVariable String serviceId,
                                    @RequestBody Object config) throws Exception {
        if(acceptFromPost){
            frontendConfigHolder.setConfigByServiceId(serviceId,config);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
