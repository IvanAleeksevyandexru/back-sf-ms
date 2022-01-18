package ru.gosuslugi.pgu.identification.core.api.dto;

import lombok.Data;
import ru.gosuslugi.pgu.identification.smart.engine.dto.RecognizeResponse;

@Data
public class PassportResponse {

    private ResponseStatus status;

    private String description;

    private String faceId;

    private Integer errorCode = 0;

    private ImageQuality quality;

    private Integer score = 0;

    private RecognizeResponse recognizeResponse;

}
