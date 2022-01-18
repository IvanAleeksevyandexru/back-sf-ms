package ru.gosuslugi.pgu.draftconverter.validation.service;

import ru.gosuslugi.pgu.draftconverter.validation.exception.JsonValidationException;
import ru.gosuslugi.pgu.dto.ScenarioDto;

import java.util.Map;

/**
 * Проверяет полученный после рендеринга результат.
 */
public interface ValidationService {

    /**
     * Проверяет черновик.
     *
     * @param json черновик в виде JSON.
     * @throws JsonValidationException если возникла ошибка при конвертации из строки в объект.
     * @return DTO, десериализованный из json.
     */
    ScenarioDto validate(String json) throws JsonValidationException;

    // Проверка на формат json
    Map<Object, Object> validateJson(String json) throws JsonValidationException;
}
