package ru.gosuslugi.pgu.pdf.template.util;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import ru.gosuslugi.pgu.common.core.exception.JsonParsingException;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EscapeUtil {
    private static final TypeReference<Object> OBJ_TYPE_REF = new TypeReference<>() { };
    private EscapeUtil() {
    }

    public static Object escapeValue(Object value, Function<String, String> escaper) {
        if (value instanceof String) {
            String escapedValue;
            try {
                Object parsedObj = JsonProcessingUtil.fromJson((String) value, OBJ_TYPE_REF);
                //Запрещаем переводить строки в числа
                if(parsedObj instanceof Number){
                    if (!value.equals(parsedObj) && StringUtils.containsAny((String) value, '\n', '\t')) {
                        return escaper.apply((String) value);
                    }
                    return value;
                }
                if(parsedObj instanceof String){
                    return escaper.apply((String) value);
                }
                if (!value.equals(parsedObj)) {
                    return escapeValue(parsedObj, escaper);
                }
                escapedValue = escaper.apply((String) value);
            } catch (JsonParsingException e) {
                escapedValue = escaper.apply((String) value);
            }
            return escapedValue;
        }
        if (value instanceof List) {
            return ((List<?>) value)
                    .stream()
                    .map(v -> EscapeUtil.escapeValue(v, escaper))
                    .collect(Collectors.toList());
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value)
                    .entrySet()
                    .stream()
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), escapeValue(entry.getValue(), escaper)))
                    .collect(HashMap::new, (m, v)-> m.put(v.getKey(), v.getValue()), HashMap::putAll);
        }
        return value;
    }
}
