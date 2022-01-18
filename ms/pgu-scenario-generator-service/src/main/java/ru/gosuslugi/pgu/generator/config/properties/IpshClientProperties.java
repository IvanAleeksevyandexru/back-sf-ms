package ru.gosuslugi.pgu.generator.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ipsh")
public class IpshClientProperties {

    private String url;

    private int maxStatusRequestCount;

    private int maxStatusRequestTimeoutMs;

}
