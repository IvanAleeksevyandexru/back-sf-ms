package ru.gosuslugi.pgu.sp.adapter.data;

import lombok.Builder;
import lombok.Getter;
import ru.atc.carcass.security.rest.model.orgs.OrgType;

import java.util.Map;
import java.util.Set;

/**
 * Контекст выполнения запроса к sp
 */
@Builder
@Getter
public class SmevRequest {
    /** Код услуги */
    private final String serviceCode;
    /** Идентификатор заявления */
    private final Long orderId;
    /** идентификатор пользователя */
    private final Long oid;
    /** идентификатор организации */
    private final Long orgId;
    /** Тип организации */
    private final OrgType orgType;
    /** тело запроса */
    private final String body;
    /** список передаваемых файлов */
    private final Set<String> files;
    /** гуид запроса */
    private final String requestGuid;
    /** заголовок авторизации */
    private final String systemAuthority;
    /** заголовок пропуска 17 статуса */
    private final Boolean skip17Status;
    /** заменитель кода идентификатора сервиса */
    private final String serviceIdCustomName;
    /** Добавочные заголовки для запроса */
    private final Map<String, String> additionalHttpHeader;
    /** Добавочный атрибут в теле запроса для отправки пользователем с доверенностью */
    private final Boolean hasEmpowerment;
    /** Добавочный атрибут в теле запроса для отправки пользователем с доверенностью */
    private final Boolean hasEmpowerment2021;
    /** id доверенности */
    private final String authorityId;
    /** Переиспользование пошлины при оплате */
    private final String reusePaymentUin;
}
