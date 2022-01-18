package ru.gosuslugi.pgu.draftconverter.context.service.impl;

import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.draftconverter.context.service.ParseService;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Реализует парсинг JSON.
 */
@Service
@Slf4j
public class JsonParseImpl implements ParseService<Object> {
    private static final TypeReference<Object> typeRef = new TypeReference<>() {
    };

    /**
     * @throws ru.gosuslugi.pgu.common.core.exception.JsonParsingException если во входных
     * параметрах передан неверный JSON.
     */
    @Override
    public Object parse(String json) {
        return JsonProcessingUtil.fromJson(json, typeRef);
    }
}
