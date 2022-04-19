package ru.gosuslugi.pgu.service.descriptor.storage.controller.model;

import lombok.Data;

import java.util.List;

@Data
public class ScenarioStatsRequest {

    private Filters filters;

    @Data
    public static class Filters {

        private List<String> items;

        private FilterType type;

    }

    public enum FilterType {

        serviceId,
        componentType

    }
}
