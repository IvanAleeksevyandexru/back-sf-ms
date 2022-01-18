package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

public interface TemplatesDataContextService {


    /**
     * Базовый метод подготовки контекста для шаблонов
     * Используется для обычных запросов
     * Подготавливает контест, удаляет ранее сгенерированные файлы (xml и pdf),
     * производит проверку файлов в attachmentInfo и удаляет лишние
     * @param serviceId
     * @param orderId
     * @param oid
     * @param roleId
     * @param draft
     * @param orgId
     * @return
     */
    TemplatesDataContext prepareRequestParameters(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft, Long orgId, Boolean skip17Status);

    /**
     * Метод по созданию контекста без удаления уже сгенерированных файлов запроса в смэв
     * Используется в случае обрабтки заявлений с УКЭП
     * @param serviceId
     * @param orderId
     * @param oid
     * @param roleId
     * @param draft
     * @param orgId
     * @return
     */
    TemplatesDataContext prepareRequestParametersWithoutRemoveOldFiles(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft, Long orgId,  Boolean skip17Status);

    /**
     * Внутренниий метод по подготовки контекста включая информацию из ServiceDescriptor
     * @param serviceId
     * @param orderId
     * @param oid
     * @param roleId
     * @param draft
     * @return
     */
    TemplatesDataContext prepareDataContext(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft);


    /**
     * Метод для подготовки контекста для шаблонов на основании данных из черновика,
     * включая ряд внутренний генерируемых атрибутов
     * @param serviceId
     * @param orderId
     * @param oid
     * @param roleId
     * @param draft
     * @return
     */
    TemplatesDataContext prepareDraftParameters(String serviceId, Long orderId, Long oid, String roleId, ScenarioDto draft);

}
