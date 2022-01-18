package ru.gosuslugi.pgu.sp.adapter.client.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.logging.annotation.Log;
import ru.gosuslugi.pgu.dto.Descriptor;
import ru.gosuslugi.pgu.sd.storage.config.ServiceDescriptorClientProperties;
import ru.gosuslugi.pgu.sp.adapter.client.SpServiceDescriptorClient;

import java.util.Map;
import java.util.Objects;

/**
 * Клиент дескриптора сервисов, реализация
 */
@Log
@Slf4j
@RequiredArgsConstructor
@Service
public class SpServiceDescriptorClientImpl implements SpServiceDescriptorClient {

    public static final String SP_CONFIG_API_PATH = "/sp/config/{serviceId}";

    public static final String SP_PARAMS_API_PATH = "/sp/params/{serviceId}";

    private final RestTemplate restTemplate;

    private final ServiceDescriptorClientProperties properties;

    @Override
    public Descriptor getSpConfig(String serviceId) {
        ResponseEntity<Descriptor> responseEntity = restTemplate.exchange(properties.getUrl() + SP_CONFIG_API_PATH,
                HttpMethod.GET, null, Descriptor.class, serviceId);
        if (Objects.nonNull(responseEntity.getBody())) {
            log.info("Получена секция spConfig сервиса {}", serviceId);
            return responseEntity.getBody();
        }
        log.info("Секция spConfig для сервиса {} не найдена", serviceId);
        return null;
    }

    @Override
    public Map<String, String> getSpParams(String serviceId) {
        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(properties.getUrl() + SP_PARAMS_API_PATH,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                },
                serviceId);
        if (Objects.nonNull(responseEntity.getBody())) {
            log.info("Получена секция parameters сервиса {}", serviceId);
            return responseEntity.getBody();
        }
        log.info("Секция parameters для сервиса {} не найдена", serviceId);
        return null;
    }

}
