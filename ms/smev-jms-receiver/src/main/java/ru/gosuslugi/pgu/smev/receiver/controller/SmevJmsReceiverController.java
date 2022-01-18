package ru.gosuslugi.pgu.smev.receiver.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.kindergarten.KinderGartenRequestDto;
import ru.gosuslugi.pgu.dto.kindergarten.KinderGartenResponseDto;
import ru.gosuslugi.pgu.smev.receiver.jms.SmevJmsMessageSender;

import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.debug;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/receiver", produces = "application/json", consumes = "application/json")
public class SmevJmsReceiverController {

    private final SmevJmsMessageSender smevJmsMessageSender;

    @PostMapping("/send")
    @Operation(summary = "Получение данных из СМЭВ по детским садам по запросу KinderGartenRequestDto")
    public KinderGartenResponseDto sendFromFormService(@Validated @RequestBody KinderGartenRequestDto request) {
        debug(log, () -> String.format("Sending to smev: %s", request.toString()));
        return smevJmsMessageSender.sendMessage(request);
    }
}
