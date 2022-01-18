package ru.gosuslugi.pgu.draftconverter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурирует документацию API.
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info().title("Draft Converter REST API Documentation")
            .description("Draft Converter REST API Documentation")
            .version("1.0");
    }
}
