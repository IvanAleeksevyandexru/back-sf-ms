package ru.gosuslugi.pgu.identification.smart.engine.dto;

import lombok.Data;
import ru.gosuslugi.pgu.identification.smart.engine.dto.inner.FieldData;

import java.util.Map;

@Data
public class RecognizeResponse {

    private Map<String, FieldData> fields;
    private byte[] personPhoto;

}
