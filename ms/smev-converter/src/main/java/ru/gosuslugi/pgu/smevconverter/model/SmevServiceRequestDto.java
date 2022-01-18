package ru.gosuslugi.pgu.smevconverter.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmevServiceRequestDto {

    String data;
    String smevVersion;
    Integer timeout;
    Integer ttl;
}
