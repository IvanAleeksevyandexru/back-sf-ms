package ru.gosuslugi.pgu.smevconverter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.smevconverter.client.SmevClient;
import ru.gosuslugi.pgu.smevconverter.client.SmevClientImpl;
import ru.gosuslugi.pgu.smevconverter.client.SmevClientStub;

@Configuration
@EnableConfigurationProperties(SmevClientProperties.class)
public class SmevClientConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "smev-service", name = "enabled", havingValue = "true")
    public SmevClient smevClient(RestTemplate restTemplate, SmevClientProperties properties) {
        return new SmevClientImpl(restTemplate, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "smev-service", name = "enabled", havingValue = "false", matchIfMissing = true)
    public SmevClient smevClientStub() {
        return new SmevClientStub();
    }
}
