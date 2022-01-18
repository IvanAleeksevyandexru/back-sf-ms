package ru.gosuslugi.pgu.smevconverter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.gosuslugi.pgu.smevconverter.client.SmevClient;
import ru.gosuslugi.pgu.smevconverter.model.SmevServiceResponseDto;

@Slf4j
@RequiredArgsConstructor
public class SmevClientStub implements SmevClient {

    @Override
    public SmevServiceResponseDto get(String requestXml) {
        log.debug("Stub SmevClient works");
        return null;
    }
}
