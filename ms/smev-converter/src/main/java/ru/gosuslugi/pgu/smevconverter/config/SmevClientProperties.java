package ru.gosuslugi.pgu.smevconverter.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@FieldDefaults(makeFinal=false, level= AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "smev-service")
public class SmevClientProperties {

    String url;
    String smevVersion;
    Integer timeout;
    Integer ttl;
}
