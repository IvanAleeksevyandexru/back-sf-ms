package ru.gosuslugi.pgu.sp.adapter.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Параметры запроса на генерацию и сохранение файла PDF
 */
@Data
public class SavedPdfRequest {
    /** Префикс файла vm шаблона, роль Applicant */
    @NotEmpty(message = "prefix должен указан и не должен быть пустым")
    private String prefix;
    /** Идентификатор черновика */
    @NotNull(message = "orderId должен быть указан")
    private Long orderId;
    /** Название файла для сохранения */
    @NotEmpty(message = "Название должьно быть указано")
    private String savedName;
    /** флаг генерации из простого шаблона */
    private Boolean light;
    /** идентификатор (oid) пользователя */
    @NotNull(message = "Идентификатор пользователя должен быть указан")
    private long userId;
    /** роль пользователя, может не передаваться */
    private String userRole;
    private Long orgId;
}
