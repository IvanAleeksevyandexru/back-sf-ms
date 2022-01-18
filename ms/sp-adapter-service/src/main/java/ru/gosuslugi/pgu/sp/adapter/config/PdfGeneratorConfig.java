package ru.gosuslugi.pgu.sp.adapter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.sp.adapter.config.props.PdfGeneratorProperties;
import ru.gosuslugi.pgu.sp.adapter.service.SmevPdfService;
import ru.gosuslugi.pgu.sp.adapter.service.impl.PdfGeneratorClientImpl;
import ru.gosuslugi.pgu.sp.adapter.service.impl.SmevPdfServiceImpl;

import static java.lang.Boolean.TRUE;

@Configuration
@EnableConfigurationProperties(PdfGeneratorProperties.class)
public class PdfGeneratorConfig {

    @Bean
    public SmevPdfService smevPdfService(RestTemplate restTemplate, PdfGeneratorProperties properties) {
        if (properties.getEnabled() == TRUE) {
            return new PdfGeneratorClientImpl(restTemplate, properties);
        }
        return new SmevPdfServiceImpl();
    }

}
