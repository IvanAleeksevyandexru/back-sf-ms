package ru.gosuslugi.pgu.sp.adapter.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pgu")
public class PguClientProperties {
    @Value("${pgu.uddi.url}")
    private String uddiUrl;
}
