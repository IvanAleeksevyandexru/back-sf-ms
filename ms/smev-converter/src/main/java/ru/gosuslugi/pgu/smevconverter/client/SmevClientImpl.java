package ru.gosuslugi.pgu.smevconverter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.smevconverter.config.SmevClientProperties;
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokPushResponseDto;
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokResponseDto;
import ru.gosuslugi.pgu.smevconverter.model.SmevServiceRequestDto;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SmevClientImpl implements SmevClient {

    private final RestTemplate restTemplate;
    private final SmevClientProperties properties;
    private final String apiPath;

    @Override
    public ResponseEntity<BarbarbokResponseDto> get(String requestXml) {
        return postToBarbarbok(requestXml, "get", BarbarbokResponseDto.class);
    }

    @Override
    public ResponseEntity<BarbarbokPushResponseDto> push(String requestXml) {
        return postToBarbarbok(requestXml, "push", BarbarbokPushResponseDto.class);
    }

    @Override
    public ResponseEntity<BarbarbokResponseDto> pull(String requestId) {
        try {
            return restTemplate.exchange(
                    properties.getUrl() + apiPath + "pull?id={requestId}",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>(){},
                    Map.of("requestId", requestId)
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    private <T> ResponseEntity<T> postToBarbarbok(String requestXml, String action, Class<T> returnType) {
        var requestDto = new SmevServiceRequestDto(requestXml, properties.getSmevVersion(), properties.getTimeout(), properties.getTtl());
        try {
            return restTemplate.exchange(
                    properties.getUrl() + apiPath + action,
                    HttpMethod.POST,
                    new HttpEntity<>(requestDto),
                    returnType
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }
}
