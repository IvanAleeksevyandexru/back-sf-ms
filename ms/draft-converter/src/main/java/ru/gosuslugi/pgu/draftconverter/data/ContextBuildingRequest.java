package ru.gosuslugi.pgu.draftconverter.data;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Параметры формирования данных для заполнения шаблона.
 */
@Data
@Builder
public class ContextBuildingRequest {
    private final String serviceId;
    private final String xmlData;
    private final Map<String, Object> answers;
    private String jsonData;
    private String fileName;
}
