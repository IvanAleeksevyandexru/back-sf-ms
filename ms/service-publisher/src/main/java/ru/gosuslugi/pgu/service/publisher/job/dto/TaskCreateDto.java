package ru.gosuslugi.pgu.service.publisher.job.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskCreateDto {

    private String serviceId;
    private String serviceVersion;
    private String targetEnvironment;
    private Boolean sendDescriptor;
    private Boolean sendTemplates;
    private Boolean sendConfig;
    private Boolean revertOnFail;
    private String revertToVersion;

    @JsonIgnore
    private byte[] descriptor;
    @JsonIgnore
    private byte[] templates;
    @JsonIgnore
    private byte[] config;

}
