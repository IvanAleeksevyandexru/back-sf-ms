package ru.gosuslugi.pgu.service.publisher.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gosuslugi.pgu.service.publisher.job.model.TaskStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfoDto {

    private Long id;
    private String serviceId;
    private String serviceVersion;
    private String targetEnvironment;
    private TaskStatus status;
    private String created;
    private String updated;
    private Boolean sendDescriptor;
    private Boolean sendTemplates;
    private Boolean sendConfig;
    private Boolean revertOnFail;
    private String revertToVersion;

}
