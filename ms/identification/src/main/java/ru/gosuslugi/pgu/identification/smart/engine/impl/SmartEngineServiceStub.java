package ru.gosuslugi.pgu.identification.smart.engine.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.gosuslugi.pgu.identification.smart.engine.SmartEngineService;
import ru.gosuslugi.pgu.identification.smart.engine.dto.RecognizeResponse;
import ru.gosuslugi.pgu.terrabyte.client.model.FileInfo;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "smart-engine.enabled", havingValue = "false")
public class SmartEngineServiceStub implements SmartEngineService {
    @Override
    public RecognizeResponse getPassportData(FileInfo fileInfo) {
        return new RecognizeResponse();
    }
}
