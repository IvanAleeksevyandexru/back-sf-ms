package ru.gosuslugi.pgu.identification.core.api.dto;

import lombok.Data;
import ru.gosuslugi.pgu.terrabyte.client.model.FileInfo;

import java.util.List;

@Data
public class VideoRequest {

    private FileInfo snapshot;

    private String faceId;

    private String selfieFaceId;

}
