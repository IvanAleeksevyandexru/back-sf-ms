package ru.gosuslugi.pgu.generator.service.fines;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.gosuslugi.pgu.dto.descriptor.FieldComponent;
import ru.gosuslugi.pgu.dto.descriptor.ScreenDescriptor;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.generator.exception.DescriptorGenerationException;
import ru.gosuslugi.pgu.generator.model.ReasonTemplate;
import ru.gosuslugi.pgu.generator.model.ScreenNumberEnum;
import ru.gosuslugi.pgu.generator.model.appeal.scenario.GetAppealScenarioResponse;
import ru.gosuslugi.pgu.generator.model.scenario.AttachmentTemplateType;
import ru.gosuslugi.pgu.generator.model.scenario.EvidenceTemplateType;
import ru.gosuslugi.pgu.generator.util.DecisionScreenUtils;

import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.gosuslugi.pgu.generator.model.ParticipationEnum.ASK;
import static ru.gosuslugi.pgu.generator.model.ScreenNumberEnum.*;
import static ru.gosuslugi.pgu.generator.util.DecisionScreenUtils.addDecisionScreen;
import static ru.gosuslugi.pgu.generator.util.DescriptorUtils.*;
import static ru.gosuslugi.pgu.generator.util.SourceXmlUtils.*;

@Component
@RequiredArgsConstructor
public class AppealDescriptorGenerator {

    public ServiceDescriptor generateMainScenario(ServiceDescriptor descriptor, GetAppealScenarioResponse xmlResponse) {
        boolean top2GroupExist = isTop2GroupExist(xmlResponse.getScenario().getAppealScenario().getReasons());
        boolean otherGroupExist = isOtherGroupExist(xmlResponse.getScenario().getAppealScenario().getReasons());

        List<ReasonTemplate> reasons = parseReasons(xmlResponse.getScenario().getAppealScenario().getReasons());

        // Основной сценарий. Экран 1. Разводящий экран
        addTopComponent(descriptor, reasons, TOP1, top2GroupExist || otherGroupExist);

        if (top2GroupExist) { // Основной сценарий. Экран 2. Разводящий экран
            addTopComponent(descriptor, reasons, TOP2, otherGroupExist);
        }

        if (otherGroupExist) { // Основной сценарий. Экран 3. Разводящий экран
            if (!top2GroupExist) {
                switchTop1RuleToOtherScreen(descriptor);
            }
            List<Map<String, String>> answers = getAnswersForScreen(reasons, OTHER);

            descriptor.getFieldComponentById(getMenuComponentId(OTHER)).orElseThrow()
                    .getAttrs().put("answers", answers);
        }

        // Основной сценарий. Экран 8.1 и далее. Загрузка доказательств
        // Пройтись по каждому возможному варианту выбора и сформировать компоненты и цепочку рулов для него
        for (ReasonTemplate reason : reasons) {
            addDecisionScreen(descriptor, reason);
            createRulesForReason(descriptor, reason);
        }

        return descriptor;
    }

    private void addTopComponent(ServiceDescriptor descriptor, List<ReasonTemplate> reasons,
                                 ScreenNumberEnum topEnum, boolean addNextScreenButton) {
        List<Map<String, String>> answers = getAnswersForScreen(reasons, topEnum);
        if (addNextScreenButton) {
            answers.add(nextScreenAnswer());
        }

        descriptor.getFieldComponentById(getMenuComponentId(topEnum)).orElseThrow()
                .getAttrs().put("answers", answers);
    }

    private void createRulesForReason(ServiceDescriptor descriptor, ReasonTemplate reason) {
        List<String> parentScreenIds = List.of("PCS6Email", "PCS3IP", "PCS4UL");

        int evidenceScreenCount = 0;
        for (List<EvidenceTemplateType> evidencePage : reason.getEvidencePages()) {
            evidenceScreenCount++;
            String screenId = "FUS" + reason.getCode() + "_" + evidenceScreenCount;

            createAndAddEvidenceScreen(descriptor,
                    getHeader(reason.getSubject(), evidenceScreenCount, reason.getEvidencePages().size()),
                    reason, evidenceScreenCount, evidencePage, screenId);

            addRuleToParentScreen(descriptor, reason, parentScreenIds, screenId, evidenceScreenCount == 1);

            parentScreenIds = List.of(screenId);
        }
        addRuleToParentScreen(descriptor, reason, parentScreenIds, calcScreenAfterReason(reason), evidenceScreenCount == 0);
    }

    public void createAndAddEvidenceScreen(ServiceDescriptor descriptor,
                                                       String header,
                                                       ReasonTemplate reason,
                                                       int evidenceScreenCount,
                                                       List<EvidenceTemplateType> evidencePage,
                                                       String screenId) {
        boolean isEvidenceRequired = isEvidenceRequired(evidencePage);
        ScreenDescriptor screen = createEvidenceScreen(screenId, header, isEvidenceRequired);

        FieldComponent fileUploadComponent = null;
        for (EvidenceTemplateType evidence : evidencePage) {
            boolean fileAttachmentExist = isFileAttachmentEvidence(evidence);
            if (!fileAttachmentExist && !evidence.isTextAllowed()) {
                throw new DescriptorGenerationException("Unsupported format on evidence with code " + evidence.getCode());
            }
            if (fileAttachmentExist) {
                String title = evidence.getTitle();
                if (!isEvidenceRequired) {
                    title += " (при наличии)";
                }
                if (fileUploadComponent == null) {
                    fileUploadComponent = createFileUploadComponent()
                            .id("fu" + "_" + reason.getCode() + "_" + evidence.getCode() + "_" + evidenceScreenCount)
                            .label(title)
                            .build();
                    descriptor.getApplicationFields().add(fileUploadComponent);
                    screen.getComponentIds().add(fileUploadComponent.getId());
                    fileUploadComponent.getAttrs().put("minFileCount", calcFileCount(evidencePage, AttachmentTemplateType::getCountMin));
                    fileUploadComponent.getAttrs().put("hideTotalAvailableCount", false);
                    fileUploadComponent.getAttrs().put("hideTotalAvailableSize", false);
                }
                ((List) fileUploadComponent.getAttrs().get("uploads"))
                        .add(prepareFileAttrsForEvidence(reason.getCode(), evidence, title));

                // set first FileUploadComponent maxSize attribute
                // according to max available attachment size for particular reason
                if (evidenceScreenCount == 1) {
                    int maxSizeForReason = DecisionScreenUtils.calcAttachmentMaxSizeForReasonInKb(reason) * 1024; // to bytes (for frontend)
                    int maxFileCountForReason = DecisionScreenUtils.calcAttachmentMaxCountForReason(reason);
                    fileUploadComponent.getAttrs().put("maxSize", maxSizeForReason);
                    fileUploadComponent.getAttrs().put("maxFileCount", maxFileCountForReason);
                }
            }
            if (evidence.isTextAllowed()) {
                FieldComponent textComponent = createTextComponent()
                        .id("ta" + "_" + reason.getCode() + "_" + evidence.getCode() + "_" + evidenceScreenCount)
                        .name(evidence.getTitle())
                        .label("<br><br><span style='font-size:16px;'>Добавьте комментарий</span>")
                        .required(evidence.isRequired())
                        .build();
                descriptor.getApplicationFields().add(textComponent);
                screen.getComponentIds().add(textComponent.getId());
            }
        }
        descriptor.getScreens().add(screen);
    }

    private String calcScreenAfterReason(ReasonTemplate reason) {
        if (reason.getParticipation() == ASK) {
            return "QSParticipation";
        }
        return "QSSignQuestion";
    }

    private String getHeader(String subject, int currentPage, int totalPages) {
        boolean hasSubject = StringUtils.hasText(subject);
        if (hasSubject && currentPage == 1) {
            return subject;
        }
        int correctedTotalPages = hasSubject ? totalPages - 1 : totalPages;
        int correctedCurrentPage = hasSubject ? currentPage - 1 : currentPage;

        if (correctedTotalPages > 1) {
            return "Загрузите доказательство (" + correctedCurrentPage + "/" + correctedTotalPages + ")";
        }
        return "Загрузите доказательство";
    }

    private int calcFileCount(List<EvidenceTemplateType> evidencePage, ToIntFunction<AttachmentTemplateType> countFunction) {
        int sumOfValues = evidencePage.stream()
                .filter(this::isFileAttachmentEvidence)
                .flatMap(evidence -> evidence.getAttachmentsAllowed().getAttachmentTemplates().stream())
                .mapToInt(countFunction)
                .sum();
        if (sumOfValues == 0) {
            boolean hasRequired = evidencePage.stream()
                    .filter(this::isFileAttachmentEvidence)
                    .anyMatch(EvidenceTemplateType::isRequired);
            if (hasRequired) {
                sumOfValues = 1;
            }
        }
        return sumOfValues;
    }

    private boolean isEvidenceRequired(List<EvidenceTemplateType> evidencePage) {
        return evidencePage.stream().anyMatch(EvidenceTemplateType::isRequired);
    }

    private boolean isFileAttachmentEvidence(EvidenceTemplateType evidence) {
        return evidence.getAttachmentsAllowed() != null
                && !isEmpty(evidence.getAttachmentsAllowed().getAttachmentTemplates());
    }

    private void switchTop1RuleToOtherScreen(ServiceDescriptor descriptor) {
        descriptor.getScreenRules().get(getMenuScreenId(TOP1)).get(0)
                .setNextDisplay(getMenuScreenId(OTHER));
    }

}
