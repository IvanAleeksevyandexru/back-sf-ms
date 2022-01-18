package ru.gosuslugi.pgu.generator.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;
import ru.gosuslugi.pgu.dto.descriptor.FieldComponent;
import ru.gosuslugi.pgu.dto.descriptor.RuleCondition;
import ru.gosuslugi.pgu.dto.descriptor.ScreenDescriptor;
import ru.gosuslugi.pgu.dto.descriptor.ScreenRule;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.dto.descriptor.types.ComponentType;
import ru.gosuslugi.pgu.dto.descriptor.types.ScreenButton;
import ru.gosuslugi.pgu.dto.descriptor.types.ScreenType;
import ru.gosuslugi.pgu.generator.exception.DescriptorGenerationException;
import ru.gosuslugi.pgu.generator.model.DecisionScreenTypeEnum;
import ru.gosuslugi.pgu.generator.model.ParticipationEnum;
import ru.gosuslugi.pgu.generator.model.ReasonTemplate;
import ru.gosuslugi.pgu.generator.model.scenario.AttachmentTemplateType;
import ru.gosuslugi.pgu.generator.model.scenario.EvidenceTemplateType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static ru.gosuslugi.pgu.generator.model.DecisionScreenTypeEnum.*;
import static ru.gosuslugi.pgu.generator.util.DescriptorUtils.getMenuComponentId;
import static ru.gosuslugi.pgu.generator.util.DescriptorUtils.getMenuScreenId;

/**
 * Класс с методам для генерации экрана решения
 */
@UtilityClass
public class DecisionScreenUtils {

    private static final String DECISION_SCREEN_RULE_TEMPLATE_ID = "DECISION_SCREEN_RULE_TEMPLATE";

    private static final String PARTICIPATION_PLACEHOLDER = "$PARTICIPATION$";
    private static final String NON_REQUIRED_EVIDENCE_LIST_PLACEHOLDER = "$NON_REQUIRED_EVIDENCE_LIST$";
    private static final String REQUIRED_EVIDENCE_LIST_PLACEHOLDER = "$REQUIRED_EVIDENCE_LIST$";
    private static final String ATTACHMENTS_MAX_SIZE_PLACEHOLDER = "$ATTACHMENTS_MAX_SIZE$";

    private static final String NO_EVIDENCES_COMPONENT_TEXT = "<p>Результат будет направлен в ваш личный кабинет. Ведомство может запросить дополнительные материалы после подачи жалобы</p>$PARTICIPATION$<p><b> Срок рассмотрения</b></p><p> <a id='tenDays'>10 дней</a></p>";
    private static final String ONLY_NON_REQUIRED_EVIDENCES_COMPONENT_TEXT = "<p>Добавьте доказательства, чтобы повысить вероятность успешного обжалования:</p><ul>$NON_REQUIRED_EVIDENCE_LIST$ <p><h6 class='yellow-line mt-24'>Проверьте объём файлов</h6><p class='fs-sm'>Приложите файлы общим размером не более $ATTACHMENTS_MAX_SIZE$ Мб</p></ul><p class='mt-8'>Ведомство может запросить дополнительные материалы после подачи жалобы</p> <p>Доказательства помогут подробнее понять обстоятельства дела. Они не имеют заранее установленной силы: <a id='KOAP2611'>ст. 26.11 КоАП РФ</a></p>$PARTICIPATION$<p><b>Срок рассмотрения</b></p> <p class='mt-12'><a id='tenDays'>10 дней</a></p> <p class='mt-12'>Результат будет направлен в ваш личный кабинет</p>";
    private static final String ONLY_REQUIRED_EVIDENCES_COMPONENT_TEXT = "<p>Для подачи жалобы приложите доказательства:</p><ul><li>Обязательные<ul>$REQUIRED_EVIDENCE_LIST$</ul></li><p><h6 class='yellow-line mt-24'>Проверьте объём файлов</h6><p class='fs-sm'>Приложите файлы общим размером не более $ATTACHMENTS_MAX_SIZE$ Мб</p></p></ul><p class='mt-8'>Ведомство может запросить дополнительные материалы после подачи жалобы</p><p>Доказательства помогут подробнее понять обстоятельства дела. Они не имеют заранее установленной силы: <a id='KOAP2611'>ст. 26.11 КоАП РФ</a></p>$PARTICIPATION$<p><b> Срок рассмотрения</b></p> <p class='mt-12'><a id='tenDays'>10 дней</a></p> <p class='mt-12'>Результат будет направлен в ваш личный кабинет</p>";
    private static final String BOTH_EVIDENCES_COMPONENT_TEXT = "<p>Для подачи жалобы приложите доказательства:</p><ul><li>Обязательные<ul>$REQUIRED_EVIDENCE_LIST$</ul></li><li>Дополнительные, при наличии<ul>$NON_REQUIRED_EVIDENCE_LIST$</ul></li><p><h6 class='yellow-line mt-24'>Проверьте объём файлов</h6><p class='fs-sm'>Приложите файлы общим размером не более $ATTACHMENTS_MAX_SIZE$ Мб</p></p></ul><p class='mt-8'>Ведомство может запросить дополнительные материалы после подачи жалобы</p><p>Доказательства помогут подробнее понять обстоятельства дела. Они не имеют заранее установленной силы: <a id='KOAP2611'>ст. 26.11 КоАП РФ</a></p>$PARTICIPATION$<p><b> Срок рассмотрения</b></p> <p class='mt-12'><a id='tenDays'>10 дней</a></p> <p class='mt-12'>Результат будет направлен в ваш личный кабинет</p>";

    private static final Map<String, Object> TEN_DAYS_ATTR = Map.of(
            "title", "10 дней на рассмотрение жалобы",
            "text", "<p>Дни отсчитываются от даты, когда жалоба со всеми материалами поступила на рассмотрение должностному лицу</p><br><p>Если десятый день приходится на нерабочий, срок рассмотрения жалобы заканчивается в следующий за ним рабочий день</p><br><p><a target='_blank'href='http://www.consultant.ru/document/cons_doc_LAW_34661/061d712f0b454cdffee47c3369341cc62fee1dd0/'>ч.1 ст. 30.5</a> и <a target='_blank'href='http://www.consultant.ru/document/cons_doc_LAW_34661/b8f629419db82a0aa9d9f712c9a593f3235005ce/'>ч.3 ст. 4.8</a> КоАП РФ</p>"
    );
    private static final Map<String, Object> KOAP2611_ATTR = Map.of(
            "title", "Ст. 26.11 КоАП РФ. Оценка доказательств",
            "text", "Судья, члены коллегиального органа, должностное лицо, осуществляющие производство по делу об административном правонарушении, оценивают доказательства по своему внутреннему убеждению, основанному на всестороннем, полном и объективном исследовании всех обстоятельств дела в их совокупности. Никакие доказательства не могут иметь заранее установленную силу."
    );

    public static void addDecisionScreen(ServiceDescriptor descriptor, ReasonTemplate reason) {
        ScreenDescriptor decisionScreen = ScreenDescriptor.builder()
                .id("IS_" + reason.getCode())
                .name("Экран решения для причины с кодом " + reason.getCode())
                .type(ScreenType.INFO)
                .header("Обжалование штрафа")
                .cssClass("gray-theme")
                .buttons(List.of(ScreenButton.builder()
                        .label("Подать жалобу")
                        .value("Подать жалобу")
                        .type("nextStep")
                        .action("getNextScreen")
                        .build()))
                .build();

        FieldComponent component = createDecisionComponent(reason);

        descriptor.getScreens().add(decisionScreen);
        descriptor.getApplicationFields().add(component);
        decisionScreen.setComponentIds(List.of(component.getId()));

        addRules(descriptor, reason, decisionScreen.getId());
    }

    public static int calcAttachmentMaxSizeForReasonInKb(ReasonTemplate reason) {
        return calcAttachmentAttributeSumForReason(reason, AttachmentTemplateType::getSizeTotalKB);
    }

    public static int calcAttachmentMaxCountForReason(ReasonTemplate reason) {
        return calcAttachmentAttributeSumForReason(reason, AttachmentTemplateType::getCountMax);
    }

    private static int calcAttachmentAttributeSumForReason(ReasonTemplate reason, ToIntFunction<AttachmentTemplateType> attributeFunction) {
        return reason.getEvidencePages().stream()
                .flatMap(Collection::stream)
                .filter(DecisionScreenUtils::hasAttachmentTemplates)
                .flatMap(evidenceTemplateType -> evidenceTemplateType.getAttachmentsAllowed().getAttachmentTemplates().stream())
                .mapToInt(attributeFunction)
                .sum();
    }

    private static boolean hasAttachmentTemplates(EvidenceTemplateType evidenceTemplateType) {
        return Objects.nonNull(evidenceTemplateType.getAttachmentsAllowed())
                && !CollectionUtils.isEmpty(evidenceTemplateType.getAttachmentsAllowed().getAttachmentTemplates());
    }

    private static void addRules(ServiceDescriptor descriptor, ReasonTemplate reason, String decisionScreenId) {
        String parentScreenId = getMenuScreenId(reason.getScreenNumber());
        descriptor.getScreenRules().get(parentScreenId).add(
                ScreenRule.builder()
                        .conditions(Set.of(RuleCondition.builder()
                                .field(getMenuComponentId(reason.getScreenNumber()))
                                .visited(true)
                                .value(reason.getCode())
                                .build()))
                        .nextDisplay(decisionScreenId)
                        .build());
        descriptor.getScreenRules().put(decisionScreenId, descriptor.getScreenRules().get(DECISION_SCREEN_RULE_TEMPLATE_ID));
    }

    private static FieldComponent createDecisionComponent(ReasonTemplate reason) {
        var decisionScreenType = getDecisionScreenType(reason);
        String componentId = "d_" + reason.getCode() + reason.getParticipation().getSuffix();
        String attachmentMaxSizeForReason = String.valueOf(calcAttachmentMaxSizeForReasonInKb(reason) / 1024); // Kb -> Mb
        switch (decisionScreenType) {
            case NO_EVIDENCES:
                return FieldComponent.builder()
                        .id(componentId)
                        .type(ComponentType.InfoScr)
                        .label(NO_EVIDENCES_COMPONENT_TEXT
                                .replace(PARTICIPATION_PLACEHOLDER, getParticipationText(reason.getParticipation())))
                        .attrs(Map.of("clarifications", Map.of("tenDays", TEN_DAYS_ATTR)))
                        .build();
            case ONLY_NON_REQUIRED_EVIDENCES:
                return FieldComponent.builder()
                        .id(componentId)
                        .type(ComponentType.InfoScr)
                        .label(ONLY_NON_REQUIRED_EVIDENCES_COMPONENT_TEXT
                                .replace(PARTICIPATION_PLACEHOLDER, getParticipationText(reason.getParticipation()))
                                .replace(NON_REQUIRED_EVIDENCE_LIST_PLACEHOLDER, getNonRequiredEvidenceText(reason))
                                .replace(ATTACHMENTS_MAX_SIZE_PLACEHOLDER, attachmentMaxSizeForReason))
                        .attrs(Map.of("clarifications", Map.of(
                                "tenDays", TEN_DAYS_ATTR,
                                "KOAP2611", KOAP2611_ATTR
                        )))
                        .build();
            case ONLY_REQUIRED_EVIDENCES:
                return FieldComponent.builder()
                        .id(componentId)
                        .type(ComponentType.InfoScr)
                        .label(ONLY_REQUIRED_EVIDENCES_COMPONENT_TEXT
                                .replace(PARTICIPATION_PLACEHOLDER, getParticipationText(reason.getParticipation()))
                                .replace(REQUIRED_EVIDENCE_LIST_PLACEHOLDER, getRequiredEvidenceText(reason))
                                .replace(ATTACHMENTS_MAX_SIZE_PLACEHOLDER, attachmentMaxSizeForReason))
                        .attrs(Map.of("clarifications", Map.of(
                                "tenDays", TEN_DAYS_ATTR,
                                "KOAP2611", KOAP2611_ATTR
                        )))
                        .build();
            case BOTH_EVIDENCES:
                return FieldComponent.builder()
                        .id(componentId)
                        .type(ComponentType.InfoScr)
                        .label(BOTH_EVIDENCES_COMPONENT_TEXT
                                .replace(PARTICIPATION_PLACEHOLDER, getParticipationText(reason.getParticipation()))
                                .replace(NON_REQUIRED_EVIDENCE_LIST_PLACEHOLDER, getNonRequiredEvidenceText(reason))
                                .replace(REQUIRED_EVIDENCE_LIST_PLACEHOLDER, getRequiredEvidenceText(reason))
                                .replace(ATTACHMENTS_MAX_SIZE_PLACEHOLDER, attachmentMaxSizeForReason))
                        .attrs(Map.of("clarifications", Map.of(
                                "tenDays", TEN_DAYS_ATTR,
                                "KOAP2611", KOAP2611_ATTR
                        )))
                        .build();
            default: throw new DescriptorGenerationException("DecisionScreenType is unknown for " + decisionScreenType);
        }
    }

    private static DecisionScreenTypeEnum getDecisionScreenType(ReasonTemplate reason) {
        boolean hasRequiredEvidence = reason.getEvidencePages().stream()
                .flatMap(Collection::stream)
                .anyMatch(EvidenceTemplateType::isRequired);
        boolean hasNonRequiredEvidence = reason.getEvidencePages().stream()
                .flatMap(Collection::stream)
                .anyMatch(evidence -> !evidence.isRequired());
        if (!hasRequiredEvidence && !hasNonRequiredEvidence) {
            return NO_EVIDENCES;
        }
        if (hasNonRequiredEvidence && !hasRequiredEvidence) {
            return ONLY_NON_REQUIRED_EVIDENCES;
        }
        if (!hasNonRequiredEvidence) {
            return ONLY_REQUIRED_EVIDENCES;
        }
        return BOTH_EVIDENCES;
    }

    private String getRequiredEvidenceText(ReasonTemplate reason) {
        return joinEvidenceTitlesToText(reason, EvidenceTemplateType::isRequired);
    }

    private String getNonRequiredEvidenceText(ReasonTemplate reason) {
        return joinEvidenceTitlesToText(reason, evidence -> !evidence.isRequired());
    }

    private String joinEvidenceTitlesToText(ReasonTemplate reason, Predicate<EvidenceTemplateType> filter) {
        return reason.getEvidencePages().stream()
                .flatMap(Collection::stream)
                .filter(filter)
                .map(EvidenceTemplateType::getTitle)
                .collect(Collectors.joining("</li><li>", "<li>", "</li>"));
    }

    private String getParticipationText(ParticipationEnum participation) {
        switch (participation) {
            case REQUIRED:
                return "<p> Жалоба будет рассмотрена в вашем присутствии.</p>";
            case NOT_REQUIRED:
                return "<p> Жалоба будет рассмотрена без вас.</p>";
            default:
                return "";
        }
    }

}
