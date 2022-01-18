package ru.gosuslugi.pgu.service.publisher.vcs.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class GitTagDto {

    private String objectId;
    private String name;
    private Instant timestamp;
    private String description;

}
