package ru.gosuslugi.pgu.identification.core.api.dto;

import lombok.Data;

@Data
public class VideoResponse {

    private ResponseStatus status;

    private String description;

    private String faceId;

    private String selfieFaceId;

    private Integer errorCode = 0;

    private Double similarityFaceId;

    private Double similaritySelfieFaceId;

}
