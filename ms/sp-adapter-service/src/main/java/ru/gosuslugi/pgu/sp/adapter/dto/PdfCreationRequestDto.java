package ru.gosuslugi.pgu.sp.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.gosuslugi.pgu.dto.ScenarioDto;

@Data
@Schema(title = "Запрос на создание PDF файла")
public class PdfCreationRequestDto {

    @Schema(title = "DTO со ScenarioDTO")
    private ScenarioDto scenarioDto;
}
