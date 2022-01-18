package ru.gosuslugi.pgu.sp.adapter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO по передаче данных в SP
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceProcessingDto {
    /** перечень мнемоник файлов, которые нужно отправить в ведомство */
    private Set<String> files;
    /** тело запроса - транспортная xml */
    private String body;
    /** дополнительные настройки (e.g. настройки подписи для доверенности) */
    private AdditionalParametersDto additionalParam;
}
