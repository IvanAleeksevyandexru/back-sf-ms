package ru.gosuslugi.pgu.xmlservice.context.data;

import lombok.Data;

/**
 * Параметры формирования данных для заполнения шаблона.
 */
@Data
public class ContextBuildingRequest {
    private final String serviceId;
    private final Long orderId;
    private final Long oid;
    private final String roleId;
    private Long orgId;
}
