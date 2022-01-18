package ru.gosuslugi.pgu.xmlservice.config;

import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.core.interceptor.creator.RestTemplateCreator;
import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурация внешних REST-клиентов.
 */
@Configuration
public class ClientConfig {
    private static final String REST_CLIENT_CONF_PREFIX = "rest-client";

    @Bean
    public RestTemplate restTemplate(ConfigurableEnvironment env,
            RestTemplateCustomizer... customizers) {
        final RestTemplate restTemplate = RestTemplateCreator.create(REST_CLIENT_CONF_PREFIX,
                JsonProcessingUtil.getObjectMapper(), env, customizers);
        addXmlMediaTypeToByteArrayMessageConverter(restTemplate.getMessageConverters());
        return restTemplate;
    }

    @Bean
    public AttachmentService attachmentService(TerrabyteClient terrabyteClient) {
        return new AttachmentService(terrabyteClient);
    }

    private void addXmlMediaTypeToByteArrayMessageConverter(
            final List<HttpMessageConverter<?>> messageConverters) {
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            if (!ByteArrayHttpMessageConverter.class
                    .isAssignableFrom(messageConverter.getClass())) {
                continue;
            }
            ByteArrayHttpMessageConverter byteArrayConverter =
                    (ByteArrayHttpMessageConverter) messageConverter;
            final List<MediaType> supportedMediaTypesExtended =
                    new ArrayList<>(messageConverter.getSupportedMediaTypes());
            supportedMediaTypesExtended.add(MediaType.TEXT_XML);
            supportedMediaTypesExtended.add(MediaType.APPLICATION_XML);
            byteArrayConverter.setSupportedMediaTypes(supportedMediaTypesExtended);
        }
    }
}
