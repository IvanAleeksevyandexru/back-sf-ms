package ru.gosuslugi.pgu.identification.core.api.dto;

import lombok.Data;
import ru.gosuslugi.pgu.terrabyte.client.model.FileInfo;

@Data
public class SelfieRequest {

    private FileInfo selfie;

    private String faceId;
}
