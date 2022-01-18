package ru.gosuslugi.pgu.identification.smart.engine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smart-engine")
public class SmartEngineProperties {

    private boolean enabled;
    private String configPath;
    private String documentType;
    private String token;

}
