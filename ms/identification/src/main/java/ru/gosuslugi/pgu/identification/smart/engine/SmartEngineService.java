package ru.gosuslugi.pgu.identification.smart.engine;

import ru.gosuslugi.pgu.identification.smart.engine.dto.RecognizeResponse;
import ru.gosuslugi.pgu.terrabyte.client.model.FileInfo;

public interface SmartEngineService {

    RecognizeResponse getPassportData(FileInfo fileInfo);

}
