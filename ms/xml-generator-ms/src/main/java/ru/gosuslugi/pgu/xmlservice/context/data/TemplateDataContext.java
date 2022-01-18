package ru.gosuslugi.pgu.xmlservice.context.data;

import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Инкапсулирует данные, необходимые для формирования запроса на создание XML-файла.
 */
@Data
public class TemplateDataContext {
    public static final String ROLE_KEY = "role";
    public static final String SERVICE_INFO_KEY = "serviceInfo";
    public static final String ROLE_INDEX_KEY = "roleIndex";
    public static final String ROLE_COMPONENT_KEY = "roleComponent";
    public static final String SP_REQUEST_GUID_KEY = "sp_request_guid";
    public static final String SP_REQUEST_HASH_KEY = "sp_request_hash";
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Object> additionalValues = new HashMap<>();
    private final Map<String, String> serviceParameters = new HashMap<>();
    /**
     * Код услуги.
     */
    private String serviceId;
    /**
     * Номер заявления.
     */
    private Long orderId;
    /**
     * Уникальный идентификатор пользователя.
     */
    private Long oid;
    /**
     * Роль пользователя.
     */
    private String roleId;
    /**
     * Параметры создаваемого файла.
     */
    private FileDescription fileDescription;

    public String getRoleId() {
        return (String) values.get(ROLE_KEY);
    }
}
