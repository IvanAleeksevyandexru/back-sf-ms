package ru.gosuslugi.pgu.identification.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gosuslugi.pgu.identification.core.api.dto.ImageQuality;
import ru.gosuslugi.pgu.identification.core.api.dto.PassportResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.ResponseStatus;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoResponse;
import ru.gosuslugi.pgu.identification.luna.dto.FaceIdResponse;
import ru.gosuslugi.pgu.identification.luna.dto.MatchResponse;
import ru.gosuslugi.pgu.identification.luna.dto.MatchVideoResponse;
import ru.gosuslugi.pgu.identification.luna.dto.inner.DetectionQuality;

@Mapper
public interface PassportIdentificationMapper {

    @Mapping(target = "status", expression = "java( getResponseStatusFromError(response.getFirstError().getErrorCode()) )")
    @Mapping(target = "faceId", expression = "java( response.getFirstFaceId() )")
    @Mapping(target = "quality", expression = "java( toResponseQuality(response.getQuality() == null ? response.getFirstQuality() : response.getQuality()) )")
    @Mapping(target = "score", expression = "java( response.getFirstScore() )")
    @Mapping(target = "errorCode", expression = "java( response.getFirstError().getErrorCode())")
    @Mapping(target = "description", expression = "java( response.getFirstError().getDesc())")
    PassportResponse toPassportResponse(FaceIdResponse response);

    ImageQuality toResponseQuality(DetectionQuality detectionQuality);

    @Mapping(target = "status", expression = "java( getResponseStatusFromError(response.getError().getErrorCode()) )")
    @Mapping(target = "selfieFaceId", expression = "java( response.getSelfieFaceId() )")
    @Mapping(target = "faceId", expression = "java( response.getFaceId() )")
    @Mapping(target = "quality", expression = "java( toResponseQuality(response.getQuality()) )")
    @Mapping(target = "similarity", expression = "java( response.getSimilarity() )")
    @Mapping(target = "errorCode", expression = "java( response.getError().getErrorCode())")
    @Mapping(target = "description", expression = "java( response.getError().getDesc())")
    @Mapping(target = "score", expression = "java( response.getScore() )")
    SelfieResponse toSelfieResponse(MatchResponse response);

    @Mapping(target = "status", expression = "java( getResponseStatusFromError(response.getError().getErrorCode()) )")
    @Mapping(target = "faceId", expression = "java( response.getFaceId() )")
    @Mapping(target = "selfieFaceId", expression = "java( response.getSelfieFaceId() )")
    @Mapping(target = "errorCode", expression = "java( response.getError().getErrorCode())")
    @Mapping(target = "description", expression = "java( response.getError().getDesc())")
    @Mapping(target = "similarityFaceId", expression = "java( response.getSimilarityPassport() )")
    @Mapping(target = "similaritySelfieFaceId", expression = "java( response.getSimilaritySelfie() )")
    VideoResponse toVideoResponse(MatchVideoResponse response);

    default ResponseStatus getResponseStatusFromError(Integer errorCode) {
        if (errorCode == 0) return ResponseStatus.SUCCESS;
        if (errorCode == -2) return ResponseStatus.NO_FACE;
        return ResponseStatus.FAILED;
    }

}
