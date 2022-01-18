package ru.gosuslugi.pgu.service.publisher.job.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.service.publisher.job.dto.servicecatalog.PublishResponse;

@Component
@RequiredArgsConstructor
public class ServiceCatalogRestClient {

    private static final String SERVICE_CONFIG_PUBLISH_PATH = "/v1/publish";
    private final RestTemplate restTemplate;

    @Value("${job-service.service-catalog.publisher.url}")
    private String url;

    public PublishResponse sendServiceConfig(JsonNode config) {
        return restTemplate.postForObject(
                url + SERVICE_CONFIG_PUBLISH_PATH,
                config,
                PublishResponse.class);
    }
}
