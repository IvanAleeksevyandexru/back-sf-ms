package ru.gosuslugi.pgu.identification.core.api.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.identification.core.api.dto.PassportRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.PassportResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoResponse;
import ru.gosuslugi.pgu.identification.core.service.IdentificationService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/identification", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class IdentificationController {

    private final IdentificationService identificationService;

    @PostMapping(value = "/passport")
    public PassportResponse passportIdentification(@RequestBody PassportRequest passportRequest) {
        return identificationService.passportIdentification(passportRequest);
    }

    @PostMapping(value = "/selfie")
    public SelfieResponse selfieIdentification(@RequestBody SelfieRequest selfieRequest) {
        return identificationService.selfieIdentification(selfieRequest);
    }

    @PostMapping(value = "/video")
    public VideoResponse videoIdentification(@RequestBody VideoRequest videoRequest) {
        return identificationService.videoIdentification(videoRequest);
    }

}
