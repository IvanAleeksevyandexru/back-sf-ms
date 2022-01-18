package ru.gosuslugi.pgu.sp.adapter.types;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PackageProcessingStatus {
    private String serviceId;
    private ProcessingStatus status;
    private String statusDescription;
    private String processedOn;
}
