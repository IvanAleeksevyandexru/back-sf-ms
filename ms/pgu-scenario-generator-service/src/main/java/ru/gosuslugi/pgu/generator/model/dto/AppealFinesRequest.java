package ru.gosuslugi.pgu.generator.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
public class AppealFinesRequest {

    @NotEmpty
    private String serviceId;

    @NotEmpty
    private String routeNumber;

    @NotEmpty
    private String billNumber;

    @NotEmpty
    private String billDate;

    @NotEmpty
    @ToString.Exclude
    private String token;

}
