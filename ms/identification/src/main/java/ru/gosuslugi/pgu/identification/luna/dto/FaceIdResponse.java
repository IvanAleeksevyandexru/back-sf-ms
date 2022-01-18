package ru.gosuslugi.pgu.identification.luna.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import ru.gosuslugi.pgu.identification.luna.dto.inner.DetectionQuality;
import ru.gosuslugi.pgu.identification.luna.dto.inner.Event;
import ru.gosuslugi.pgu.identification.luna.dto.inner.FilteredDetection;
import ru.gosuslugi.pgu.identification.luna.dto.inner.Image;
import ru.gosuslugi.pgu.identification.luna.dto.inner.LunaError;

import java.util.List;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceIdResponse {

    private List<Image> images;

    private List<Event> events;

    private DetectionQuality quality;

    @JsonProperty("filtered_detections")
    private FilteredDetection filteredDetections;

    public String getFirstFaceId() {
        if (!CollectionUtils.isEmpty(events))
            return events.get(0).getFaceId();
        return Strings.EMPTY;
    }

    public DetectionQuality getFirstQuality() {
        if (!CollectionUtils.isEmpty(events))
            return events.get(0).getQuality();
        return new DetectionQuality();
    }

    public LunaError getFirstError() {
        if (!CollectionUtils.isEmpty(images))
            return images.get(0).getError();
        return LunaError.getUnknownLunaError();
    }

    public Integer getFirstScore() {
        if (!CollectionUtils.isEmpty(events))
            return events.get(0).getScore();

        if (Objects.nonNull(filteredDetections))
            return filteredDetections.getFirstObjectValue();

        return 0;
    }

    public static FaceIdResponse createDefault(){
        var faceIdResponse = new FaceIdResponse();
        var image = new Image();
        var imageError = new LunaError();

        image.setError(imageError);
        faceIdResponse.setImages(List.of(image));

        return faceIdResponse;
    }

    public static FaceIdResponse createWithEmptyPhotoError(){
        var faceIdResponse = createDefault();
        var imageError = LunaError.getEmptyPhotoLunaError();
        faceIdResponse.getImages().get(0).setError(imageError);

        return faceIdResponse;
    }

    public static FaceIdResponse createWithNoFaceError(){
        var faceIdResponse = createDefault();
        var imageError = LunaError.getNoFaceError();
        faceIdResponse.getImages().get(0).setError(imageError);
        return faceIdResponse;
    }

}
