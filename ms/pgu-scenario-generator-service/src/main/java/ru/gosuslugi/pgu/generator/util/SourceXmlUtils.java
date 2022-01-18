package ru.gosuslugi.pgu.generator.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import ru.gosuslugi.pgu.generator.model.ReasonTemplate;
import ru.gosuslugi.pgu.generator.model.ScreenNumberEnum;
import ru.gosuslugi.pgu.generator.model.appeal.cycle.AppealCycleResponse;
import ru.gosuslugi.pgu.generator.model.scenario.AttachmentFileFormatType;
import ru.gosuslugi.pgu.generator.model.scenario.AttachmentTemplateType;
import ru.gosuslugi.pgu.generator.model.scenario.EvidenceTemplateListType;
import ru.gosuslugi.pgu.generator.model.scenario.EvidenceTemplateType;
import ru.gosuslugi.pgu.generator.model.scenario.ReasonTemplateType;
import ru.gosuslugi.pgu.generator.model.scenario.ReasonTemplatesType;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.gosuslugi.pgu.generator.model.ParticipationEnum.*;
import static ru.gosuslugi.pgu.generator.util.MapUtils.linkedMapOf;
import static ru.gosuslugi.pgu.generator.util.MapUtils.linkedMapOfObjects;

/**
 * Утилитарные методы по работе с XML от ведомства
 */
@UtilityClass
public class SourceXmlUtils {

    public static List<ReasonTemplate> parseReasons(ReasonTemplatesType reasonTemplates) {
        List<ReasonTemplate> reasons = new ArrayList<>();

        reasons.addAll(getReasons(reasonTemplates.getTop1().getReasons().getReasons(), ScreenNumberEnum.TOP1));
        if (reasonTemplates.getTop2() != null) {
            reasons.addAll(getReasons(reasonTemplates.getTop2().getReasons().getReasons(), ScreenNumberEnum.TOP2));
        }
        if (reasonTemplates.getOther() != null) {
            reasons.addAll(getReasons(reasonTemplates.getOther().getReasons().getReasons(), ScreenNumberEnum.OTHER));
        }

        return reasons;
    }

    private static List<ReasonTemplate> getReasons(List<ReasonTemplateType> reasons, ScreenNumberEnum screenNumber) {
        if (isEmpty(reasons)) {
            return List.of();
        }
        return reasons.stream().map(
                reason -> ReasonTemplate.builder()
                        .code(reason.getCode())
                        .screenNumber(screenNumber)
                        .value(reason.getTitle())
                        .evidencePages(prepareEvidences(reason.getEvidences()))
                        .subject(reason.getSubject() == null ? null : reason.getSubject().getHint())
                        .participation(
                                reason.getParticipation().isReadOnly() != Boolean.TRUE ? ASK :
                                        reason.getParticipation().isDefaultValue() ? REQUIRED : NOT_REQUIRED)
                        .build()
        ).collect(Collectors.toList());
    }

    public static List<Map<String, String>> getAnswersForScreen(List<ReasonTemplate> reasons, ScreenNumberEnum screenNumber) {
        return reasons.stream()
                .filter(reason -> reason.getScreenNumber() == screenNumber)
                .map(reason -> linkedMapOf("label", reason.getValue(),
                        "value", reason.getCode(),
                        "type", "nextStep",
                        "action", "getNextScreen"))
                .collect(Collectors.toList());
    }

    public static boolean isTop2GroupExist(ReasonTemplatesType reasonGroups) {
        return reasonGroups.getTop2() != null
                && reasonGroups.getTop2().getReasons() != null
                && !isEmpty(reasonGroups.getTop2().getReasons().getReasons());
    }

    public static boolean isOtherGroupExist(ReasonTemplatesType reasonGroups) {
        return reasonGroups.getOther() != null
                && reasonGroups.getOther().getReasons() != null
                && !isEmpty(reasonGroups.getOther().getReasons().getReasons());
    }

    public static Set<String> parseFileTypes(AttachmentTemplateType attachment) {
        return attachment.getFileFormats().getFileFormats().stream()
                .flatMap(fileFormat -> fileFormat.getFileExtensions().getFileExtensions().stream())
                .collect(Collectors.toSet());
    }

    public static Map<String, Object> prepareFileAttrsForEvidence(String reasonCode, EvidenceTemplateType evidence, String title) {
        AttachmentTemplateType attachment = evidence.getAttachmentsAllowed().getAttachmentTemplates().get(0);
        Map<String, Object> attrs = linkedMapOfObjects(
                "uploadId", "documents_" + reasonCode + "_" + evidence.getCode(),
                "type", "single",
                "title", title,
                "label", createUploaderLabel(evidence.getHint(), attachment),
                "maxSize", attachment.getSizeTotalKB() * 1024,
                "fileType", parseFileTypes(attachment)
        );
        if (attachment.getCountMax() > 0) {
            attrs.put("maxFileCount", attachment.getCountMax());
        }
        if (attachment.getCountMin() > 0) {
            attrs.put("minFileCount", attachment.getCountMin());
        } else {
            attrs.put("required", false);
        }
        return attrs;
    }

    private static String createUploaderLabel(String hint, AttachmentTemplateType attachment) {
        String fileTypes = attachment.getFileFormats().getFileFormats().stream()
                .map(AttachmentFileFormatType::getTitle)
                .map(String::toLowerCase)
                .collect(Collectors.joining(", "));
        return (Objects.nonNull(hint))? (hint +"<br><br>") : "" + "Дополнительно можно прикрепить: " + fileTypes + ".";
    }

    public static ReasonTemplate getReasonForAdditionalSteps(AppealCycleResponse additionalDocsRequest) {
        return ReasonTemplate.builder()
                .code("add")
                .evidencePages(prepareEvidences(additionalDocsRequest.getAdditionalEvidenceRequest() == null ? null
                        : additionalDocsRequest.getAdditionalEvidenceRequest().getEvidences()))
                .build();
    }

    private static List<List<EvidenceTemplateType>> prepareEvidences(EvidenceTemplateListType evidences) {
        if (evidences == null || isEmpty(evidences.getEvidences())) {
            return List.of();
        }

        Map<String, List<EvidenceTemplateType>> result = new LinkedHashMap<>();
        int nullGroupCounter = 0;

        for (EvidenceTemplateType evidence : evidences.getEvidences()) {
            if (StringUtils.isEmpty(evidence.getRequireGroup())) {
                result.put("empty_" + (nullGroupCounter++), List.of(evidence));
            } else {
                result.compute(evidence.getRequireGroup(), (key, oldValue) -> {
                    List<EvidenceTemplateType> evidenceList = oldValue == null ? new ArrayList<>() : oldValue;
                    evidenceList.add(evidence);
                    return evidenceList;
                });
            }
        }

        return new ArrayList<>(result.values());
    }

}
