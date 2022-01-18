package ru.gosuslugi.pgu.voskhod.adapter.service.esep.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.ArrayOfCertificateUserInfo;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CertificateUserInfo;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CreateUIToSingResponse;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.FileCertificateUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.dto.esep.PrepareSignRequest;
import ru.gosuslugi.pgu.voskhod.adapter.service.esep.EsepServiceHelper;
import ru.nvg.idecs.storageservice.ws.types.EsepFile;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@ConditionalOnProperty(value = "esep-service.enabled", havingValue = "false")
@RequiredArgsConstructor
public class EsepServiceHelperMock implements EsepServiceHelper {

    @Value("${esep-service.mock-url:#{null}}")
    private String esepMockUrl;

    private final RestTemplate restTemplate;

    @Override
    public CreateUIToSingResponse createUIToSingEx(String returnUrl, List<EsepFile> fileAccessCodes) {
        requireNonNull(returnUrl, "returnUrl is empty");

        PrepareSignRequest dto = new PrepareSignRequest();
        dto.setReturnUrl(returnUrl);
        try {
            ResponseEntity<CreateUIToSingResponse> response = restTemplate.exchange(
                    esepMockUrl + "/api/sign/createUIToSing", HttpMethod.POST,
                    new HttpEntity<>(dto),
                    CreateUIToSingResponse.class
            );
            return response.getBody();
        } catch(RestClientException e) {
            throw new ExternalServiceException(e);
        }
    }

    @Override
    public List<FileCertificateUserInfo> getFileCertificateUserInfos(List<String> fileAccessCodes) {
        List<FileCertificateUserInfo> result = new ArrayList<>();
        fileAccessCodes.forEach(code -> {
            FileCertificateUserInfo fileCertificateUserInfo = new FileCertificateUserInfo();
            fileCertificateUserInfo.setFileAccessCode(code);

            ArrayOfCertificateUserInfo certificatesUserInfo1 = new ArrayOfCertificateUserInfo();
            CertificateUserInfo certificateUserInfo1 = new CertificateUserInfo();
            certificateUserInfo1.setSNILS("112-233-446 96");
            certificateUserInfo1.setCommonName("Каменев Игорь Витальевич");
            certificatesUserInfo1.getCertificateUserInfo().add(certificateUserInfo1);
            fileCertificateUserInfo.setCertificatesUserInfo(certificatesUserInfo1);

            ArrayOfCertificateUserInfo certificatesUserInfo2 = new ArrayOfCertificateUserInfo();
            CertificateUserInfo certificateUserInfo2 = new CertificateUserInfo();
            certificateUserInfo2.setSNILS("000-429-429 38");
            certificateUserInfo2.setCommonName("Курочкина Инна Сергеевна");
            certificatesUserInfo2.getCertificateUserInfo().add(certificateUserInfo2);
            fileCertificateUserInfo.setCertificatesUserInfo(certificatesUserInfo2);

            result.add(fileCertificateUserInfo);
        });


        return result;
    }
}
