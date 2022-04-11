package ru.gosuslugi.pgu.service.descriptor.storage.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class PutScenarioResponse {
    @Schema(
            description = "ID сервиса"
    )
    private final String serviceId;

    @Schema(
            description = "Время обновления"
    )
    private final Instant updated;
}
