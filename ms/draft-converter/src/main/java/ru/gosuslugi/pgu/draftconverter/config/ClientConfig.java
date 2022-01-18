package ru.gosuslugi.pgu.draftconverter.config;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.core.interceptor.creator.RestTemplateCreator;

/**
 * Конфигурация внешних REST-клиентов.
 */
@Configuration
public class ClientConfig {
    private static final String REST_CLIENT_CONF_PREFIX = "rest-client";

    @Bean
    public RestTemplate restTemplate(ConfigurableEnvironment env, RestTemplateCustomizer... customizers) {
        return RestTemplateCreator.create(REST_CLIENT_CONF_PREFIX, JsonProcessingUtil.getObjectMapper(),env, customizers);
    }
}
