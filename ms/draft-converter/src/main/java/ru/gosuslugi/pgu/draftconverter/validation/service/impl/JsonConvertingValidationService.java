package ru.gosuslugi.pgu.draftconverter.validation.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.core.exception.JsonParsingException;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.draftconverter.validation.exception.JsonValidationException;
import ru.gosuslugi.pgu.draftconverter.validation.service.ValidationService;
import ru.gosuslugi.pgu.dto.ScenarioDto;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class JsonConvertingValidationService implements ValidationService {

    /**
     * Пытается преобразовать к {@link ScenarioDto}.
     */
    @Override
    public ScenarioDto validate(String json) throws JsonValidationException {
        if (Objects.isNull(json)) {
            throw new JsonValidationException("Передана нулевая ссылка");
        }
        try {
            return JsonProcessingUtil.fromJson(json, ScenarioDto.class);
        } catch (JsonParsingException e) {
            final String errorSummary = "Результат не удалось преобразовать к ScenarioDto: ";
            log.error(errorSummary + json, e);
            throw new JsonValidationException(errorSummary + e.getMessage(), e);
        }
    }

    /**
     * Попытка преобразования к произвольному json
     */
    @Override
    public Map<Object, Object> validateJson(String json) throws JsonValidationException {
        if (Objects.isNull(json)) {
            throw new JsonValidationException("Передана нулевая ссылка");
        }

        try {
            return JsonProcessingUtil.fromJson(json, new TypeReference<>() {});
        } catch (JsonParsingException e) {
            final String errorSummary = "Не удалось преобразовать json к объекту - некорректный формат";
            log.error(errorSummary + ": " + e.getMessage(), e);
            throw new JsonValidationException(errorSummary, e);
        }
    }
}
