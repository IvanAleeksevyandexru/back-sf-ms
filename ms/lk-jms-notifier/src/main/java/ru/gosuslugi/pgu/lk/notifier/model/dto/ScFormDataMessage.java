package ru.gosuslugi.pgu.lk.notifier.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScFormDataMessage {

    private String name;
    private String value;
    private String mnemonic;

}
