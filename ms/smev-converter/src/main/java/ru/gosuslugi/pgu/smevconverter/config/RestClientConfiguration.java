package ru.gosuslugi.pgu.smevconverter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.core.interceptor.creator.RestTemplateCreator;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestTemplate restTemplate(ConfigurableEnvironment env, RestTemplateCustomizer... customizers) {
        return RestTemplateCreator.create("rest-client", objectMapper(), env, customizers);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonProcessingUtil.getObjectMapper();
    }
}
