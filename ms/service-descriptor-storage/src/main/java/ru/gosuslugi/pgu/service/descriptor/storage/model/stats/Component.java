package ru.gosuslugi.pgu.service.descriptor.storage.model.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Component {

    private String componentType;
    private int amount;

}
