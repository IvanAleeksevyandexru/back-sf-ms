package ru.gosuslugi.pgu.smevconverter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.SmevConverterGetRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPullRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPushRequestDto;
import ru.gosuslugi.pgu.ratelimit.client.RateLimitService;
import ru.gosuslugi.pgu.smevconverter.model.UserSession;
import ru.gosuslugi.pgu.smevconverter.service.SmevConverterService;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/services", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class SmevConverterController {

    private final UserSession userSession;
    private final RateLimitService rateLimitService;
    private final SmevConverterService smevConverterService;

    @PostMapping("/get")
    @Operation(summary = "Получить сведения из барбарбока и преобразовать xml в json по указанному шаблону", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "429", description = "Превышение лимита обращений к сервису пользователя"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public ResponseEntity<Map<Object, Object>> get(@Parameter(description = "Запрос на получение json", required = true)
                                                       @Valid @RequestBody SmevConverterGetRequestDto request) {
        checkAccess(request.getServiceId());
        return smevConverterService.get(request);
    }

    @PostMapping("/push")
    @Operation(summary = "Поставить задачу в очередь барбарбока", responses = {
            @ApiResponse(responseCode = "200", description = "OK, идентификатор задачи"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "429", description = "Превышение лимита обращений к сервису пользователя"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public ResponseEntity<Map<Object, Object>> push(@Parameter(description = "Получить идентификатор задачи", required = true)
                                                        @Valid @RequestBody SmevConverterPushRequestDto request) {
        checkAccess(request.getServiceId());
        return smevConverterService.push(request);
    }

    @PostMapping("/pull")
    @Operation(summary = "Получить сведения из барбарбока по идентификатору задачи в очереди барабарбока", responses = {
            @ApiResponse(responseCode = "200", description = "OK, преобразует в json по указанному шаблону"),
            @ApiResponse(responseCode = "202", description = "Задача в обработке"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public ResponseEntity<Map<Object, Object>> pull(@Parameter(description = "Получить json по идентификатору задачи", required = true)
                                                        @Valid @RequestBody SmevConverterPullRequestDto request) {
        return smevConverterService.pull(request);
    }

    // Проверка возможности обращения в барбарбок
    private void checkAccess(String serviceCode) {
        rateLimitService.apiCheck("smev-converter-" + userSession.getUserId() + "-" + serviceCode);
    }
}
