package ru.gosuslugi.pgu.service.descriptor.storage.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.gosuslugi.pgu.service.descriptor.storage.model.stats.ServiceInfo;

import java.util.List;

@Data
@AllArgsConstructor
public class ScenarioStatsResponse {

    private List<ServiceInfo> services;

    private int totalServices;
    private int totalComponents;


}
