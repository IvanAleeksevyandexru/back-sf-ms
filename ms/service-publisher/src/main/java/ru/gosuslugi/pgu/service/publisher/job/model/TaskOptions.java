package ru.gosuslugi.pgu.service.publisher.job.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskOptions {

    private Boolean isJsonInclude;
    private Boolean isTemplateInclude;
    private Boolean isServiceConfigInclude;

    private Boolean isRevertEnabled;
    private String revertVersion;

}
