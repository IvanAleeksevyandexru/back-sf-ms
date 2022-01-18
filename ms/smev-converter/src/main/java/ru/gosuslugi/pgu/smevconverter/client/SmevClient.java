package ru.gosuslugi.pgu.smevconverter.client;

import ru.gosuslugi.pgu.smevconverter.model.SmevServiceResponseDto;

public interface SmevClient {

    SmevServiceResponseDto get(String requestXml);
}
