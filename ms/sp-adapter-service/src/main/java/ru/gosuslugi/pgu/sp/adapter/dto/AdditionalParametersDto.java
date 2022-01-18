package ru.gosuslugi.pgu.sp.adapter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Дополнительные атрибуты, которые передаются в SP, аналог additionalParam в soap
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalParametersDto {
    /**
     * Атрибут который говорит, что у пользователя есть доверенность
     * на отправку сообщений в ведомство, не являясь руководителем
     * Данный флаг завязан на фунционал генерации orderId (при создании необходимо было передать привилегии)
     * Данный атрибут имеет значение в виде строке "true", если привилегии нет, то просто не передается
     */
    private String hasEmpowerment2021;
    private Boolean hasEmpowerment = null;
    private String authorityId;
    /** Аттрибут при передачи говорит о необходимости переиспользования пошлины */
    private String reusePaymentUin;
}
