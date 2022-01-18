package ru.gosuslugi.pgu.service.publisher.job;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "job-service")
public class JobProperties {

    public static final String TEMPLATES_API_PATH = "/v1/templates/{serviceId}";
    public static final String DESCRIPTOR_API_PATH = "/v1/scenario/{serviceId}";

    private Map<String, Environment> environments;
    private Integer taskSelectMinutesCount;

    @Data
    public static class Environment {
        private String descriptorUrl;
        private String templatesUrl;
        private String configUrl;

        public String getDescriptorApiPath() {
            return descriptorUrl + DESCRIPTOR_API_PATH;
        }

        public String getTemplatesApiPath() {
            return templatesUrl + TEMPLATES_API_PATH;
        }
    }
}
