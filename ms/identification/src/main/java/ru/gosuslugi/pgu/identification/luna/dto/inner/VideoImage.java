package ru.gosuslugi.pgu.identification.luna.dto.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoImage {

    private Integer status;
    private String filename;
    private LunaError error;
    private Detection detections;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detection {
        @JsonProperty("face_detections")
        private List<FaceDetection> faceDetections;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaceDetection {
        private List<Verification> verifications;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Verification {
        private Double similarity;
        private Boolean status;
        private Face face;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Face {
        @JsonProperty("face_id")
        private String faceId;
    }

}
