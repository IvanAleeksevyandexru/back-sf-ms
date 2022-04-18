package ru.gosuslugi.pgu.service.descriptor.storage.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties("component-registry")
public class ComponentRegistryProperties {

    private Duration findQueryTimeout = Duration.ofSeconds(20);

    private Duration cacheRefreshTimeout = Duration.ofHours(1);

}
