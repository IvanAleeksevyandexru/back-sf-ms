package ru.gosuslugi.pgu.draftconverter.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Данные, необходимые для формирования запроса на создание черновика.
 */
@Data
public class TemplateDataContext {
    /**
     * Код услуги.
     */
    private String serviceId;
    /**
     * Уникальный идентификатор пользователя.
     */
    private Long oid;
    /**
     * Значения
     */
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, String> serviceParameters = new HashMap<>();
    private XmlElement xmlTree;
    private Object jsonTree;
    private String templateFileName;
}
