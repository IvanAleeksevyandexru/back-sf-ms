package ru.gosuslugi.pgu.identification.luna.dto.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("face_attributes")
    private FaceAttributes faceAttributes;

    private Face face;
    private List<Detection> detections;

    public String getFaceId(){
        return face.getFaceId();
    }

    public DetectionQuality getQuality() {
        if (!CollectionUtils.isEmpty(detections) &&
                Objects.nonNull(detections.get(0).getSamples()) &&
                Objects.nonNull(detections.get(0).getSamples().getFace()) &&
                Objects.nonNull(detections.get(0).getSamples().getFace().getDetection())
        )
            return detections.get(0).getSamples().getFace().getDetection().getQuality();
        return new DetectionQuality();
    }

    public Integer getScore() {
        if (Objects.nonNull(faceAttributes) && Objects.nonNull(faceAttributes.getScore()))
            return Double.valueOf(faceAttributes.getScore() * 100).intValue();
        return 0;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaceAttributes {
        private Double score;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Face {
        @JsonProperty("face_id")
        private String faceId;

        @JsonProperty("external_id")
        private String external_id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detection {
        private DetectionSample samples;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetectionSample {
        private DetectionSampleFace face;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetectionSampleFace {
        private DetectionSampleFaceDetection detection;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetectionSampleFaceDetection {
        private DetectionQuality quality;
    }
}
