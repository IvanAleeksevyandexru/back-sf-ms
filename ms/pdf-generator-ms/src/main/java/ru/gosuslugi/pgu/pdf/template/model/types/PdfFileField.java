package ru.gosuslugi.pgu.pdf.template.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * PDF file
 * TODO consider to move this info into main service json in SF-module
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PdfFileField {
    /**
     * Display mode
     */
    private PdfSupportedClasses className;

    /**
     * Label or in some cases title
     */
    private String label;

    /**
     * Value (will be ignored in case of Form, FormStep, Panel classes)
     */
    private String value;

    // TODO KK Переписать на mapstruct после того, как генерация pdf будет выпилена из sp-adapter
    public LinkedHashMap<String, Object> transformToMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("id", "1");
        map.put("className", this.className);
        map.put("label", this.label);
        map.put("longLabel", "");
        map.put("title", "");
        map.put("value", this.value);
        map.put("textValue", this.value);
        return map;
    }

}

