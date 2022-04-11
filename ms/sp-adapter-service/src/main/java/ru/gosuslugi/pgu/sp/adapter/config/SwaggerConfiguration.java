package ru.gosuslugi.pgu.sp.adapter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info().title("Service Processing Adapter")
                .description("Service Processing Adapter REST API документация")
                .version("1.0");
    }
}
