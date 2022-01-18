package ru.gosuslugi.pgu.service.publisher.job.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@Entity
@Table(name = "publish_task")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentTaskId;

    @Column(name = "created_at")
    private LocalDateTime created;

    @Column(name = "updated_at")
    private LocalDateTime updated;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "service_version")
    private String serviceVersion;

    @Column(name = "target_environment")
    private String targetEnvironment;

    @Column(name = "is_send_descriptor")
    private Boolean sendDescriptor;

    @Column(name = "is_send_templates")
    private Boolean sendTemplates;

    @Column(name = "is_send_config")
    private Boolean sendConfig;

    @Column(name = "is_revert_on_fail")
    private Boolean revertOnFail;

    @Column(name = "revert_to_version")
    private String revertToVersion;

    @Lob
    private byte[] descriptor;

    @Lob
    private byte[] templates;

    @Lob
    private byte[] config;

    public void setStatus(TaskStatus status) {
        if (Objects.nonNull(status)) {
            this.status = status;
            this.updated = LocalDateTime.now();
        }
    }
}
