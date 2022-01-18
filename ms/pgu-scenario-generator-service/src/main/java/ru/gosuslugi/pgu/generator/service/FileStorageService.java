package ru.gosuslugi.pgu.generator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;

import java.util.Collections;
import java.util.Map;

import static java.lang.String.format;
import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.debug;
import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.error;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final static String GET_FILE_CONTENT = "/file-content/{orderId}/15?mnemonic=appeal_result";

    @Value("${file-storage.url}")
    private String fileStorageUrl;

    private final RestTemplate restTemplate;

    public String loadXmlByOrderId(String orderId) {
        debug(log, () -> format("Find xml request for additional evidences for orderId = %s", orderId));
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    fileStorageUrl + GET_FILE_CONTENT,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    String.class,
                    Map.of("orderId", orderId));
            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            }
        } catch (Exception e) {
            throw new ExternalServiceException("External file storage throw exception: " + e.getMessage(), e);
        }
        error(log, () -> format("External file storage returns error %s", response));
        throw new ExternalServiceException("External file storage returns error code " + response.getStatusCode());
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.ALL));
        return httpHeaders;
    }
}
