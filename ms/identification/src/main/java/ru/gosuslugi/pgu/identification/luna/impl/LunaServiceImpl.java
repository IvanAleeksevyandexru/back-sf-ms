package ru.gosuslugi.pgu.identification.luna.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoRequest;
import ru.gosuslugi.pgu.identification.core.model.UserSession;
import ru.gosuslugi.pgu.identification.luna.LunaProperties;
import ru.gosuslugi.pgu.identification.luna.LunaService;
import ru.gosuslugi.pgu.identification.luna.dto.FaceIdResponse;
import ru.gosuslugi.pgu.identification.luna.dto.MatchRequest;
import ru.gosuslugi.pgu.identification.luna.dto.MatchResponse;
import ru.gosuslugi.pgu.identification.luna.dto.MatchVideoResponse;
import ru.gosuslugi.pgu.identification.luna.dto.inner.LunaError;
import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;
import ru.gosuslugi.pgu.terrabyte.client.model.FileInfo;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
@Slf4j

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(LunaProperties.class)
public class LunaServiceImpl implements LunaService {

    private final TerrabyteClient terrabyteClient;
    private final UserSession userSession;
    private final RestTemplate restTemplate;
    private final LunaProperties lunaProps;
    private final ObjectMapper objectMapper;

    @Override
    public FaceIdResponse createFaceId(FileInfo fileInfo) {
        byte[] file = terrabyteClient.getFile(fileInfo, userSession.getUserId(), userSession.getCookie());
        return createFaceId(file);
    }

    @Override
    public FaceIdResponse createFaceId(byte[] file) {
        if (Objects.isNull(file) || file.length == 0) return FaceIdResponse.createWithEmptyPhotoError();
        try {
            HttpHeaders headers = getHeadersWithType("image/jpeg");
            HttpEntity httpEntity = new HttpEntity(file, headers);
            String externalId = UUID.randomUUID().toString();
            var response = restTemplate.postForEntity(
                    lunaProps.getCreateFullPath(),
                    httpEntity,
                    FaceIdResponse.class,
                    Map.of("guid", externalId)
            );
            FaceIdResponse faceIdResponse = response.getBody();
            if (isNoFaceResponse(faceIdResponse)) {
                var withNoFaceError = FaceIdResponse.createWithNoFaceError();
                withNoFaceError.setEvents(faceIdResponse.getEvents());
                withNoFaceError.setFilteredDetections(faceIdResponse.getFilteredDetections());
                return withNoFaceError;
            }
            if (isNoEventResponse(faceIdResponse)) {
                var withNoEventError = FaceIdResponse.createWithEmptyPhotoError();
                withNoEventError.setEvents(faceIdResponse.getEvents());
                withNoEventError.setFilteredDetections(faceIdResponse.getFilteredDetections());
                withNoEventError.setQuality(faceIdResponse.getFilteredDetections().getFirstDetectionQuality());
                return withNoEventError;
            }
            return faceIdResponse;
        } catch (HttpClientErrorException ex) {
            log.error("HttpClient error during executing request", ex);
            var errorResponse = FaceIdResponse.createDefault();
            var lunaError = getLunaErrorFromException(ex);
            errorResponse.getImages().get(0).setError(lunaError);
            return errorResponse;
        } catch (Exception ex) {
            log.error("Unknown error during executing request", ex);
            var errorResponse = FaceIdResponse.createDefault();
            var lunaError = LunaError.getUnknownLunaError();
            errorResponse.getImages().get(0).setError(lunaError);
            return errorResponse;
        }
    }

    @Override
    public MatchResponse matchFaces(SelfieRequest selfieRequest) {
        FaceIdResponse faceIdResponse = createFaceId(selfieRequest.getSelfie());

        if (isNoFaceResponse(faceIdResponse)) {
            var response = new MatchResponse();
            response.setError(LunaError.getNoFaceError());
            response.setScore(faceIdResponse.getFirstScore());
            return response;
        }

        if (isNoEventResponse(faceIdResponse)) {
            var response = new MatchResponse();
            response.setError(LunaError.getBadPhotoLunaError());
            response.setScore(faceIdResponse.getFilteredDetections().getFirstObjectValue());
            response.setQuality(faceIdResponse.getQuality());
            return response;
        }

        MatchRequest matchRequest = new MatchRequest(selfieRequest.getFaceId(), faceIdResponse.getFirstFaceId());
        try {
            HttpHeaders headers = getHeadersWithType("application/json");
            HttpEntity httpEntity = new HttpEntity(matchRequest, headers);
            var response = restTemplate.postForEntity(
                    lunaProps.getMatchFacesFullPath(),
                    httpEntity,
                    MatchResponse[].class
            );

            var responseItem = response.getBody()[0];
            responseItem.setSelfieFaceId(faceIdResponse.getFirstFaceId());
            responseItem.setFaceId(selfieRequest.getFaceId());
            responseItem.setQuality(faceIdResponse.getFirstQuality());
            responseItem.setError(LunaError.getSuccess());
            responseItem.setScore(faceIdResponse.getFirstScore());
            return responseItem;
        } catch (HttpClientErrorException ex) {
            log.error("HttpClient error during executing request", ex);
            var lunaError = getLunaErrorFromException(ex);
            var matchResponse = new MatchResponse();
            matchResponse.setError(lunaError);
            return matchResponse;
        } catch (Exception ex) {
            log.error("Unknown error during executing request", ex);
            var matchResponse = new MatchResponse();
            matchResponse.setError(LunaError.getUnknownLunaError());
            return matchResponse;
        }
    }

    @SneakyThrows
    @Override
    public MatchVideoResponse matchVideo(VideoRequest videoRequest) {
        byte[] file = terrabyteClient.getFile(videoRequest.getSnapshot(), userSession.getUserId(), userSession.getCookie());
        try {
            HttpHeaders headers = getHeadersWithType("image/jpeg");
            HttpEntity httpEntity = new HttpEntity(file, headers);
            var response = restTemplate.postForEntity(
                    lunaProps.getMatchVideoFullPath(),
                    httpEntity,
                    MatchVideoResponse.class,
                    Map.of("passportId", videoRequest.getFaceId(),
                            "selfieId", videoRequest.getSelfieFaceId())
            );
            var responseItem = response.getBody();
            responseItem.setFaceId(videoRequest.getFaceId());
            responseItem.setSelfieFaceId(videoRequest.getSelfieFaceId());
            return responseItem;
        } catch (HttpClientErrorException ex) {
            log.error("HttpClient error during executing request", ex);
            var lunaError = getLunaErrorFromException(ex);
            var response = MatchVideoResponse.createDefault();
            response.getImages().get(0).setError(lunaError);
            return response;
        } catch (Exception ex) {
            log.error("Unknown error during executing request", ex);
            var response = MatchVideoResponse.createDefault();
            response.getImages().get(0).setError(LunaError.getUnknownLunaError());
            return response;
        }
    }

    private HttpHeaders getHeadersWithType(String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Luna-Account-Id", lunaProps.getAccountId());
        headers.add("Content-Type", contentType);
        return headers;
    }

    private LunaError getLunaErrorFromException(HttpClientErrorException ex) {
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), LunaError.class);
        } catch (JsonProcessingException e) {
            return LunaError.getUnknownLunaError();
        }
    }

    private boolean isNoFaceResponse(FaceIdResponse faceIdResponse) {
        if (Objects.isNull(faceIdResponse.getEvents()) && Objects.isNull(faceIdResponse.getFilteredDetections()))
            return true;

        if (CollectionUtils.isEmpty(faceIdResponse.getEvents())
                && (Objects.isNull(faceIdResponse.getFilteredDetections().getFaceDetections())
                        || faceIdResponse.getFilteredDetections().getFaceDetections().isEmpty()))
            return true;

        return false;
    }

    private boolean isNoEventResponse(FaceIdResponse faceIdResponse) {
        var filteredDetection = faceIdResponse.getFilteredDetections();

        if (CollectionUtils.isEmpty(faceIdResponse.getEvents())
                && Objects.nonNull(filteredDetection.getFirstDetectionQuality())
                && Objects.nonNull(filteredDetection.getFirstDetectionQuality().getBlurriness()))
            return true;

        return false;
    }

}
