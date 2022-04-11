package ru.gosuslugi.pgu.lk.notifier.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.lk.SendNotificationRequestDto;
import ru.gosuslugi.pgu.lk.notifier.jms.JmsLkMessageSender;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/notification", produces = "application/json")
public class LkNotificationController {

    private final JmsLkMessageSender jmsLkMessageSender;

    @PostMapping("/send")
    @Operation(summary = "Отсылка нотификаций", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public void sendLkNotification(@Validated @RequestBody SendNotificationRequestDto request) {
        log.debug("Sending notification to LK: {}", request);
        jmsLkMessageSender.sendMessages(request);
    }

}
