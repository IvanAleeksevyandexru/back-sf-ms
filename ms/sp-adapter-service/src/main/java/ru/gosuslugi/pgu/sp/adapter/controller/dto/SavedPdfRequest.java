package ru.gosuslugi.pgu.sp.adapter.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Параметры запроса на генерацию и сохранение файла PDF
 */
@Data
@Schema(title = "Параметры запроса на генерацию и сохранение файла PDF")
public class SavedPdfRequest {

    /** Префикс файла vm шаблона, роль Applicant */
    @Schema(title = "Префикс файла vm шаблона, роль Applicant")
    @NotEmpty(message = "prefix должен указан и не должен быть пустым")
    private String prefix;

    /** Идентификатор черновика */
    @Schema(title = "orderId должен быть указан")
    @NotNull(message = "orderId должен быть указан")
    private Long orderId;

    /** Название файла для сохранения */
    @Schema(title = "Название должьно быть указано")
    @NotEmpty(message = "Название должьно быть указано")
    private String savedName;

    /** флаг генерации из простого шаблона */
    @Schema(title = "флаг генерации из простого шаблона")
    private Boolean light;

    /** идентификатор (oid) пользователя */
    @NotNull(message = "Идентификатор пользователя должен быть указан")
    @Schema(title = "идентификатор (oid) пользователя")
    private long userId;

    /** роль пользователя, может не передаваться */
    @Schema(title = "роль пользователя, может не передаваться")
    private String userRole;

    @Schema(title = "ID компании-владелеца черновика")
    private Long orgId;
}
