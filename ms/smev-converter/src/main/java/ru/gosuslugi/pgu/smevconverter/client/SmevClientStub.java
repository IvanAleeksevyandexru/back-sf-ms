package ru.gosuslugi.pgu.smevconverter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokPushResponseDto;
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokResponseDto;

@Slf4j
@RequiredArgsConstructor
public class SmevClientStub implements SmevClient {

    @Override
    public ResponseEntity<BarbarbokResponseDto> get(String requestXml) {
        log.debug("Stub SmevClient call barbarbok#get");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BarbarbokPushResponseDto> push(String requestXml) {
        log.debug("Stub SmevClient call barbarbok#push");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BarbarbokResponseDto> pull(String requestId) {
        log.debug("Stub SmevClient call barbarbok#pull");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
