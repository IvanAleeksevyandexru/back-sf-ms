package ru.gosuslugi.pgu.identification.core.api.dto;

import lombok.Data;

@Data
public class SelfieResponse {

    private ResponseStatus status;

    private String description;

    private String faceId;

    private String selfieFaceId;

    private Integer errorCode = 0;

    private Double similarity;

    private ImageQuality quality;

    private Integer score = 0;

}
