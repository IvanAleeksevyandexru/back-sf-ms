package ru.gosuslugi.pgu.identification.luna.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.junit.Ignore;
import org.springframework.util.CollectionUtils;
import ru.gosuslugi.pgu.identification.luna.dto.inner.DetectionQuality;
import ru.gosuslugi.pgu.identification.luna.dto.inner.Image;
import ru.gosuslugi.pgu.identification.luna.dto.inner.LunaError;
import ru.gosuslugi.pgu.identification.luna.dto.inner.VideoImage;

import java.util.List;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchVideoResponse {

    private List<VideoImage> images;
    private String selfieFaceId;
    private String faceId;

    public LunaError getError() {
        if (!CollectionUtils.isEmpty(images))
            return images.get(0).getError();
        return LunaError.getUnknownLunaError();
    }

    public Double getSimilarityPassport() {
        if (!CollectionUtils.isEmpty(images) &&
                Objects.nonNull(images.get(0).getDetections()) &&
                !CollectionUtils.isEmpty(images.get(0).getDetections().getFaceDetections())
        )
            return images.get(0)
                    .getDetections()
                    .getFaceDetections()
                    .get(0).getVerifications().get(0).getSimilarity();
        return 0.0;
    }

    /**
     * В этом методе ТРЕБУЕТСЯ брать второй элемент из списка.
     * Т.К. Первый - это результат от сравнения паспорта
     * @return
     */
    public Double getSimilaritySelfie() {
        if (!CollectionUtils.isEmpty(images) &&
                Objects.nonNull(images.get(0).getDetections()) &&
                !CollectionUtils.isEmpty(images.get(0).getDetections().getFaceDetections()) &&
                Objects.nonNull(images.get(0).getDetections().getFaceDetections().get(0)) &&
                Objects.nonNull(images.get(0).getDetections().getFaceDetections().get(0).getVerifications()) &&
                !CollectionUtils.isEmpty(images.get(0).getDetections().getFaceDetections().get(0).getVerifications()) &&
                images.get(0).getDetections().getFaceDetections().get(0).getVerifications().size() > 1 &&
                Objects.nonNull(images.get(0).getDetections().getFaceDetections().get(0).getVerifications().get(1))

        )
            return images.get(0)
                    .getDetections()
                    .getFaceDetections()
                    .get(0).getVerifications().get(1).getSimilarity();
        return 0.0;
    }

    public static MatchVideoResponse createDefault(){
        var videoResponse = new MatchVideoResponse();
        var image = new VideoImage();
        var imageError = new LunaError();
        image.setError(imageError);
        videoResponse.setImages(List.of(image));
        return  videoResponse;
    }
}
