package ru.gosuslugi.pgu.voskhod.adapter.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pgu")
public class PguProperties {

    @Value("${pgu.uddi.url}")
    private String uddiUrl;
}
