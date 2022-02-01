package ru.gosuslugi.pgu.smevconverter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.dto.SmevConverterPullRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterGetRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPushRequestDto;
import ru.gosuslugi.pgu.ratelimit.client.RateLimitService;
import ru.gosuslugi.pgu.ratelimit.client.exception.RateLimitServiceException;
import ru.gosuslugi.pgu.smevconverter.model.SmevPullResponseDto;
import ru.gosuslugi.pgu.smevconverter.model.UserSession;
import ru.gosuslugi.pgu.smevconverter.service.SmevConverterService;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/services", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
public class SmevConverterController {

    private final UserSession userSession;
    private final RateLimitService rateLimitService;
    private final SpanService spanService;
    private final SmevConverterService smevConverterService;

    @PostMapping("/get")
    @Operation(summary = "Конвертирует XML в произвольный json", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "429", description = "Превышение лимита обращений к сервису пользователя"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public Map<Object, Object> get(@Parameter(description = "Запрос на получение json", required = true) @Valid @RequestBody SmevConverterGetRequestDto request) {

        checkAccess(request.getServiceId());
        return smevConverterService.get(request);
    }

    @PostMapping("/push")
    @Operation(summary = "Отправка запроса", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "429", description = "Превышение лимита обращений к сервису пользователя"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public String push(@Valid @RequestBody SmevConverterPushRequestDto request) {
        checkAccess(request.getServiceId());
        return smevConverterService.push(request);
    }

    @GetMapping("/pull")
    @Operation(summary = "Проверка статуса запроса", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "202", description = "Операция в обработке"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "429", description = "Превышение лимита обращений к сервису пользователя"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public ResponseEntity<Map<Object, Object>> pull(@Valid @RequestBody SmevConverterPullRequestDto request) {
        SmevPullResponseDto response = smevConverterService.pull(request);
        return new ResponseEntity<>(response.getResponse(), response.getStatus());
    }

    // Проверка возможности обращения в барбарбок
    private void checkAccess(String serviceCode) {
        try {
            String key = "smev-converter-" + userSession.getUserId() + "-" + serviceCode;
            rateLimitService.apiCheck(key);
        } catch (RateLimitServiceException e) {
            log.warn("Превышение лимита обращений к сервису {} пользователя {} ({}) traceId {}", serviceCode, userSession.getUserId(), e.getReason(), spanService.getCurrentTraceId());
            throw e;
        }
    }
}
