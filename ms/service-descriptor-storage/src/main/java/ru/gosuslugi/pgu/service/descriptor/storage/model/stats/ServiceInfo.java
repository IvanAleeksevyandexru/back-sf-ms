package ru.gosuslugi.pgu.service.descriptor.storage.model.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo {

    private String serviceId;
    private List<Component> components;

}
