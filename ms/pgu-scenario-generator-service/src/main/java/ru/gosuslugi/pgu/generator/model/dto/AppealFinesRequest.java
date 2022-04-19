package ru.gosuslugi.pgu.generator.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
public class AppealFinesRequest {

    @NotEmpty
    @Schema(
            description = "ID сервиса"
    )
    private String serviceId;

    @NotEmpty
    @Schema(
            description = "Номер пути"
    )
    private String routeNumber;

    @NotEmpty
    @Schema(
            description = "Номер оплаты"
    )
    private String billNumber;

    @NotEmpty
    @Schema(
            description = "дата оплаты"
    )
    private String billDate;

    @NotEmpty
    @ToString.Exclude
    @Schema(
            description = "Токен"
    )
    private String token;

}
