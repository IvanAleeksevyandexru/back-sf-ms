package ru.gosuslugi.pgu.smevconverter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.gosuslugi.pgu.smevconverter.model.SmevPullResponseDto;
import ru.gosuslugi.pgu.smevconverter.model.SmevServiceResponseDto;

@Slf4j
@RequiredArgsConstructor
public class SmevClientStub implements SmevClient {

    @Override
    public SmevServiceResponseDto get(String requestXml) {
        log.debug("Stub SmevClient get");
        return null;
    }

    @Override
    public String push(String requestXml) {
        log.debug("Stub SmevClient push");
        return null;
    }

    @Override
    public SmevPullResponseDto pull(String requestId) {
        log.debug("Stub SmevClient pull");
        return null;
    }
}
