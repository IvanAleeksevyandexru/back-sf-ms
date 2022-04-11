package ru.gosuslugi.pgu.service.descriptor.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SpParamsService;

@RestController
@RequiredArgsConstructor
public class SpParamsController {

    private final SpParamsService spParamsService;

    /**
     * GET /sp/params/{serviceId} : Получение parameters по идентификатору сервиса
     *
     * @param serviceId Идентификатор сервиса (required)
     * @return OK (status code 200) и подчасть конфига spConfig
     */
    @RequestMapping(value = "/sp/params/{serviceId}", method = RequestMethod.GET)
    @Operation(summary = "Получение parameters по идентификатору сервиса", responses = {
            @ApiResponse(responseCode = "200", description = "CRC шаблона",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    ResponseEntity<String> get(@Parameter(name = "serviceId", in = ParameterIn.PATH, description = "ID сервиса", schema = @Schema(type = "string"))
                               @PathVariable("serviceId") String serviceId) {
        String params = spParamsService.get(serviceId);
        return ResponseEntity.ok(params);
    }

}
