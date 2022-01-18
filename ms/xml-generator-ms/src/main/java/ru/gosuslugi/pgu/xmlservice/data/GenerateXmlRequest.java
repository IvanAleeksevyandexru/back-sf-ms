package ru.gosuslugi.pgu.xmlservice.data;

import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonCreator;

/**
 * Запрос на создание файла XML.
 */
@Data
@Schema(title = "Запрос на создание XML файла")
public class GenerateXmlRequest {
    /**
     * Код услуги.
     */
    @Schema(title = "Код услуги", required = true)
    @NotBlank
    private final String serviceId;

    /**
     * Номер заявления.
     */
    @Schema(title = "Номер заявления", required = true)
    @NotNull
    private final Long orderId;

    /**
     * Идентификатор пользователя.
     */
    @Schema(title = "Идентификатор пользователя", required = true)
    @NotNull
    private final Long userId;

    /**
     * Описание файла.
     */
    @Schema(title = "Описание файла", required = true)
    @NotNull
    private final FileDescription fileDescription;

    /**
     * Роль пользователя.
     */
    @Schema(title = "Роль пользователя", required = true)
    @NotBlank
    private final String roleId;

    /**
     * Черновик.
     */
    @Schema(title = "Черновик", description = "Если не указан, для получения будет произведен "
            + "внешний вызов сервиса черновиков")
    private ScenarioDto draft;

    /**
     * Идентификатор организации.
     */
    @Schema(title = "Идентификатор организации")
    private Long orgId;

    @JsonCreator
    public GenerateXmlRequest(String serviceId, Long orderId, Long userId, String roleId,
            FileDescription fileDescription) {
        this.serviceId = serviceId;
        this.orderId = orderId;
        this.userId = userId;
        this.roleId = roleId;
        this.fileDescription = fileDescription;
    }
}
