package ru.gosuslugi.pgu.service.publisher.vcs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "vcs")
public class VcsProperties {

    private String repoUrl;
    private String userName;
    private String userPass;
    private String localRepoPath;
    private String vmTemplatesDir;
    private String servicesTempPath;
    private Map<String, String> services;
    private String allServicesConfigFile;
    private Boolean configSourceEnabled;
    private String serviceConfigDir;

    public String getServiceTemplatePath(String serviceId) {
        // File.separator не подходит при локальной разработке на Windows
        return vmTemplatesDir + "/" + serviceId;
    }

    public String getServiceConfigPath(String serviceId) {
        return String.format("%s/%s_config.json", serviceConfigDir, serviceId);
    }

}
