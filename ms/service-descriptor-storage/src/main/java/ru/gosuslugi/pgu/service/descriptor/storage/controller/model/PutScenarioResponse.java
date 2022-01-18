package ru.gosuslugi.pgu.service.descriptor.storage.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class PutScenarioResponse {
    private final String serviceId;
    private final Instant updated;
}
