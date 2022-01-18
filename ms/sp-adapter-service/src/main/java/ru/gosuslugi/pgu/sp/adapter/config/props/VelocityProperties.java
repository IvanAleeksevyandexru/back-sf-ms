package ru.gosuslugi.pgu.sp.adapter.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "velocity")
public class VelocityProperties {
    private ResourceLoader resourceLoader;
    private String fileResourceLoaderPath;
    private String resourceLoaderFileCache;
    private String resourceLoaderFileModificationCheckInterval;

    private String fileResourceLoaderClass = "org.apache.velocity.runtime.resource.loader.FileResourceLoader";
    private String classResourceLoaderClass = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";

    public enum ResourceLoader {
        FILE, CLASS
    }
}
