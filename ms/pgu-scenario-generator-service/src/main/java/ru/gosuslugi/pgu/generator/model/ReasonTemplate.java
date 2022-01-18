package ru.gosuslugi.pgu.generator.model;

import lombok.Builder;
import lombok.Data;
import ru.gosuslugi.pgu.generator.model.scenario.EvidenceTemplateType;

import java.util.List;

@Data
@Builder
public class ReasonTemplate {

    private final String code;

    private final ScreenNumberEnum screenNumber;

    private final String value;

    private final String subject;

    private final ParticipationEnum participation;


    // Каждая страница может содержать одно или несколько доказательств, объединенных по requireGroup
    private final List<List<EvidenceTemplateType>> evidencePages;

}
