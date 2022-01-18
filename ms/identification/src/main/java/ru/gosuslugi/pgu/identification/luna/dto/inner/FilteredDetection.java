package ru.gosuslugi.pgu.identification.luna.dto.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilteredDetection {

    @JsonProperty("face_detections")
    private List<FaceDetection> faceDetections;

    public Integer getFirstObjectValue() {
        FaceDetection faceDetectionFirst = CollectionUtils.firstElement(faceDetections);
        if (Objects.nonNull(faceDetectionFirst)) {
            var filterReasonFirst = CollectionUtils.firstElement(faceDetectionFirst.getFilterReasons());
            if (Objects.nonNull(filterReasonFirst))
                return Double.valueOf(filterReasonFirst.getObjectValue() * 100).intValue();
        }
        return 0;
    }

    public DetectionQuality getFirstDetectionQuality() {
        FaceDetection faceDetectionFirst = CollectionUtils.firstElement(faceDetections);
        if (Objects.nonNull(faceDetectionFirst)
                && Objects.nonNull(faceDetectionFirst.getDetection())
                && Objects.nonNull(faceDetectionFirst.getDetection().getSample())
                && Objects.nonNull(faceDetectionFirst.getDetection().getSample().getDetection()))
            return faceDetectionFirst.getDetection().getSample().getDetection().getQuality();

        return new DetectionQuality();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaceDetection {

        @JsonProperty("filter_reasons")
        private List<FilterReason> filterReasons;

        private Detection detection;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FilterReason {

        @JsonProperty("filter_name")
        private String filterName;

        @JsonProperty("object_value")
        private Double objectValue;

        @JsonProperty("threshold_value")
        private Double thresholdValue;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detection {
        private Sample sample;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sample {
        private SampleDetection detection;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SampleDetection {
        private DetectionQuality quality;
    }
}
