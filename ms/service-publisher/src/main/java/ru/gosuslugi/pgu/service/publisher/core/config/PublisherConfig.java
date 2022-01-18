package ru.gosuslugi.pgu.service.publisher.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.exception.handler.RestResponseErrorHandler;
import ru.gosuslugi.pgu.common.core.interceptor.creator.RestTemplateCreator;

@Configuration
public class PublisherConfig {

    @Bean
    public RestTemplate restTemplate(ConfigurableEnvironment env, RestTemplateCustomizer... customizers) {
        RestTemplate restTemplate = RestTemplateCreator.create("rest-client", objectMapper(), env, customizers);
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
