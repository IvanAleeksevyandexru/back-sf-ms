package ru.gosuslugi.pgu.smevconverter.client;

import org.springframework.http.ResponseEntity;
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokPushResponseDto;
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokResponseDto;

// http://pgu-dev-fednlb.test.gosuslugi.ru/barbarbok/swagger-ui.html
public interface SmevClient {

    ResponseEntity<BarbarbokResponseDto> get(String requestXml);
    ResponseEntity<BarbarbokResponseDto> pull(String requestId);
    ResponseEntity<BarbarbokPushResponseDto> push(String requestXml);
}
