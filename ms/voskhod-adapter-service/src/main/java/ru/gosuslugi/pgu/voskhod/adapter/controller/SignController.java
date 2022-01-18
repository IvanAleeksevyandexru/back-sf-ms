package ru.gosuslugi.pgu.voskhod.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.esep.FileCertificateUserInfoRequest;
import ru.gosuslugi.pgu.dto.esep.FileCertificatesUserInfoResponse;
import ru.gosuslugi.pgu.dto.esep.PrepareSignRequest;
import ru.gosuslugi.pgu.dto.esep.PrepareSignResponse;
import ru.gosuslugi.pgu.voskhod.adapter.service.SignService;

@RestController
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @RequestMapping(value = "/prepareSign", method = RequestMethod.POST, produces = "application/json")
    public PrepareSignResponse prepareSign(@RequestBody PrepareSignRequest request) {
        return signService.prepareSign(request);
    }

    @RequestMapping(value = "/getFileCertificatesUserInfo", method = RequestMethod.POST, produces = "application/json")
    public FileCertificatesUserInfoResponse getFileCertificatesUserInfo(@RequestBody FileCertificateUserInfoRequest request) {
        return signService.getFileCertificatesUserInfo(request.getFileAccessCodes());
    }
}
