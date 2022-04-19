package ru.gosuslugi.pgu.service.descriptor.storage.repository.model;

import lombok.Data;

import java.util.List;

@Data
public class ServiceDescriptorProjection {

    private List<FieldComponent> applicationFields;

    @Data
    public static class FieldComponent {

        private String type;

    }

}
