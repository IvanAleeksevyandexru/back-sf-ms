package ru.gosuslugi.pgu.identification.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.identification.core.api.dto.PassportRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.PassportResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoResponse;
import ru.gosuslugi.pgu.identification.core.mapper.PassportIdentificationMapper;
import ru.gosuslugi.pgu.identification.core.service.IdentificationService;
import ru.gosuslugi.pgu.identification.luna.LunaService;
import ru.gosuslugi.pgu.identification.smart.engine.SmartEngineService;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentificationServiceImpl implements IdentificationService {

    private final LunaService lunaService;
    private final SmartEngineService smartEngineService;
    private final PassportIdentificationMapper mapper;

    @Override
    public PassportResponse passportIdentification(PassportRequest passportRequest) {
        var recognizeResponse = smartEngineService.getPassportData(passportRequest.getPassportInfo());
        var faceIdResponse = lunaService.createFaceId(recognizeResponse.getPersonPhoto());
        var passportResponse = mapper.toPassportResponse(faceIdResponse);
        passportResponse.setRecognizeResponse(recognizeResponse);
        return passportResponse;
    }

    @Override
    public SelfieResponse selfieIdentification(SelfieRequest selfieRequest) {
        var matchResponse = lunaService.matchFaces(selfieRequest);
        var response = mapper.toSelfieResponse(matchResponse);
        return response;
    }

    @Override
    public VideoResponse videoIdentification(VideoRequest videoRequest) {
        var matchVideoResponse = lunaService.matchVideo(videoRequest);
        var response = mapper.toVideoResponse(matchVideoResponse);
        return response;
    }

}
