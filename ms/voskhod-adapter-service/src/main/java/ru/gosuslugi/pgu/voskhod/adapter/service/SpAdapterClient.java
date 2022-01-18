package ru.gosuslugi.pgu.voskhod.adapter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.dto.SmevRequestDto;
import ru.gosuslugi.pgu.dto.SpAdapterDto;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
class SpAdapterClient {

    @Value("${sp.integration:#{null}}")
    private String spUrl;

    private final RestTemplate restTemplate;

    SmevRequestDto createXmlAndPdf(Long orderId, Long userId, Long orgId, String requestGuid) {
        requireNonNull(orderId, "orderId is empty");
        requireNonNull(userId, "userId is empty");
        requireNonNull(requestGuid, "requestGuid is empty");
        try {
            SpAdapterDto dto = new SpAdapterDto(null, null, orderId, userId, null, requestGuid, orgId, false);
            ResponseEntity<SmevRequestDto> response = restTemplate.exchange(
                    spUrl + "/createXmlAndPdf", HttpMethod.POST,
                    new HttpEntity<>(dto),
                    SmevRequestDto.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new ExternalServiceException(e);
        }
    }

}
