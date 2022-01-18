package ru.gosuslugi.pgu.generator.util;

import lombok.experimental.UtilityClass;
import ru.gosuslugi.pgu.dto.descriptor.FieldComponent;
import ru.gosuslugi.pgu.dto.descriptor.RuleCondition;
import ru.gosuslugi.pgu.dto.descriptor.ScreenDescriptor;
import ru.gosuslugi.pgu.dto.descriptor.ScreenRule;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.dto.descriptor.types.ComponentType;
import ru.gosuslugi.pgu.dto.descriptor.types.ScreenButton;
import ru.gosuslugi.pgu.dto.descriptor.types.ScreenType;
import ru.gosuslugi.pgu.generator.exception.DescriptorGenerationException;
import ru.gosuslugi.pgu.generator.model.ReasonTemplate;
import ru.gosuslugi.pgu.generator.model.ScreenNumberEnum;
import ru.gosuslugi.pgu.generator.model.appeal.cycle.AppealCycleResponse;
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest;
import ru.gosuslugi.pgu.generator.model.scenario.EvidenceTemplateType;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.gosuslugi.pgu.generator.util.MapUtils.linkedMapOf;
import static ru.gosuslugi.pgu.generator.util.MapUtils.linkedMapOfObjects;

@UtilityClass
public class DescriptorUtils {

    public final static String ADDITIONAL_STEP_SCREEN_PREFIX = "Add_Step_";
    public final static String ADDITIONAL_START_SCREEN_NAME = ADDITIONAL_STEP_SCREEN_PREFIX + "Start";
    public final static String ADDITIONAL_FINISH_SCREEN_NAME = ADDITIONAL_STEP_SCREEN_PREFIX + "Finish";

    private final static String ADDITIONAL_START_COMPONENT_NAME = "add_start";
    private final static String ADDITIONAL_FINISH_COMPONENT_NAME = "add_finish";

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static FieldComponent.FieldComponentBuilder createTextComponent() {
        return FieldComponent.builder()
                .type(ComponentType.TextArea)
                .attrs(linkedMapOfObjects("stringsAmount", "2",
                        "charsAmount", "4000"));
    }

    public static FieldComponent.FieldComponentBuilder createFileUploadComponent() {
        return FieldComponent.builder()
                .type(ComponentType.FileUploadComponent)
                .suggestionId("prev_files")
                .attrs(new HashMap<>(Map.of("uploads", new ArrayList<>(), "archiveTypes", List.of("SIG"))));
    }

    public static void addRuleToParentScreen(ServiceDescriptor descriptor, ReasonTemplate reason,
                                             List<String> parentScreenIds, String newScreenId, boolean isFirstScreen) {
        for (String parentScreenId : parentScreenIds) {
            List<ScreenRule> parentScreenRules = descriptor.getScreenRules().computeIfAbsent(parentScreenId, k -> new ArrayList<>());

            if (isFirstScreen) {
                parentScreenRules.add(ScreenRule.builder()
                        .conditions(Set.of(RuleCondition.builder()
                                .field(getMenuComponentId(reason.getScreenNumber()))
                                .visited(true)
                                .value(reason.getCode())
                                .build()))
                        .nextDisplay(newScreenId)
                        .build());
            } else {
                parentScreenRules.add(ScreenRule.builder()
                        .nextDisplay(newScreenId)
                        .build());
            }
        }
    }

    public static String getMenuScreenId(ScreenNumberEnum screenNumber) {
        switch (screenNumber) {
            case TOP1: return "QS1";
            case TOP2: return "QS2";
            case OTHER: return "QS3";
            default: throw new DescriptorGenerationException("ScreenNumberEnum value not found for " + screenNumber);
        }
    }

    public static String getMenuComponentId(ScreenNumberEnum screenNumber) {
        switch (screenNumber) {
            case TOP1: return "q1";
            case TOP2: return "q2";
            case OTHER: return "q3";
            default: throw new DescriptorGenerationException("ScreenNumberEnum value not found for " + screenNumber);
        }
    }

    public static void createAndAddAdditionStartScreen(ServiceDescriptor serviceDescriptor,
                                                       AppealCycleResponse additionalDocsRequest,
                                                       AppealFinesRequest request) {
        ScreenDescriptor startScreen = ScreenDescriptor.builder()
                .id(ADDITIONAL_START_SCREEN_NAME)
                .name("Стартовый экран загрузки дополнительных документов")
                .type(ScreenType.INFO)
                .header(getAdditionalDocsStartHeader(request))
                .componentIds(List.of(ADDITIONAL_START_COMPONENT_NAME))
                .buttons(List.of(ScreenButton.builder()
                        .label("Далее")
                        .value("Далее")
                        .type("nextStep")
                        .action("getNextScreen")
                        .build()))
                .build();

        FieldComponent startComponent = FieldComponent.builder()
                .id(ADDITIONAL_START_COMPONENT_NAME)
                .type(ComponentType.InfoScr)
                .label(generateStartComponentLabel(additionalDocsRequest))
                .build();
        serviceDescriptor.getScreens().add(startScreen);
        serviceDescriptor.getApplicationFields().add(startComponent);
        serviceDescriptor.setInit(startScreen.getId());
        serviceDescriptor.getInitScreens().put("Applicant", Map.of("Applicant", ADDITIONAL_START_SCREEN_NAME));
    }

    public static void createAndAddAdditionFinishScreen(ServiceDescriptor serviceDescriptor) {
        ScreenDescriptor finishScreen = ScreenDescriptor.builder()
                .id(ADDITIONAL_FINISH_SCREEN_NAME)
                .name("Финишный экран загрузки дополнительных документов")
                .type(ScreenType.INFO)
                .header("Дополнительные сведения отправлены!")
                .isTerminal(true)
                .componentIds(List.of(ADDITIONAL_FINISH_COMPONENT_NAME))
                .buttons(List.of(ScreenButton.builder()
                        .label("В личный кабинет")
                        .value("Перейти в личный кабинет")
                        .type("redirectToLK")
                        .action("getNextScreen")
                        .build()))
                .build();

        FieldComponent finishComponent = FieldComponent.builder()
                .id(ADDITIONAL_FINISH_COMPONENT_NAME)
                .type(ComponentType.InfoScr)
                .label("<p><b>Что дальше</b></p><p>Результат рассмотрения будет направлен в ваш личный кабинет</p>")
                .build();
        serviceDescriptor.getScreens().add(finishScreen);
        serviceDescriptor.getApplicationFields().add(finishComponent);
    }

    public static ScreenDescriptor createEvidenceScreen(String screenId, String header, boolean isEvidenceRequired) {
        var nextButton = ScreenButton.builder()
                .label("Далее")
                .type("nextStep")
                .action("getNextScreen")
                .build();
        var skipButton = ScreenButton.builder()
                .label("Пропустить")
                .type("skipStep")
                .action("skipStep")
                .color("white")
                .build();

        return ScreenDescriptor.builder()
                .id(screenId)
                .name("Загрузка доказательств")
                .type(ScreenType.CUSTOM)
                .header(header)
                .componentIds(new ArrayList<>())
                .buttons(isEvidenceRequired ? List.of(nextButton) : List.of(skipButton, nextButton))
                .build();
    }

    public static Map<String, String> nextScreenAnswer() {
        return linkedMapOf("label", "Другое",
                "value", "Другое",
                "type", "nextStep",
                "action", "getNextScreen");
    }

    private static String getAdditionalDocsStartHeader(AppealFinesRequest request) {
        return "Дополнительные сведения для обжалования штрафа №" + request.getBillNumber()
                + " от " + formatFineDate(request.getBillDate());
    }

    private static String generateStartComponentLabel(AppealCycleResponse additionalDocsRequest) {
        StringBuilder label = new StringBuilder();
        if (isAllRequired(additionalDocsRequest)) {
            label.append("<p>Чтобы продолжить работу над жалобой, ведомству нужны эти материалы:</p>");
        } else {
            label.append("<p>Чтобы продолжить работу над жалобой, ведомству нужен один из этих материалов:</p>");
        }
        if (!isEvidencesEmpty(additionalDocsRequest)) {
            label.append("<ul>");
            for (EvidenceTemplateType evidence : additionalDocsRequest.getAdditionalEvidenceRequest().getEvidences().getEvidences()) {
                label.append("<li>").append(evidence.getTitle()).append("</li>");
            }
            label.append("</ul>");
        }

        return label.toString();
    }

    private static boolean isEvidencesEmpty(AppealCycleResponse additionalDocsRequest) {
        return additionalDocsRequest.getAdditionalEvidenceRequest() == null
                || isEmpty(additionalDocsRequest.getAdditionalEvidenceRequest().getEvidences().getEvidences());
    }

    private static boolean isAllRequired(AppealCycleResponse additionalDocsRequest) {
        return !isEvidencesEmpty(additionalDocsRequest)
                && additionalDocsRequest.getAdditionalEvidenceRequest().getEvidences().getEvidences().stream()
                .allMatch(EvidenceTemplateType::isRequired);
    }

    private static String formatFineDate(String isoDate) {
        return dateFormatter.format(OffsetDateTime.parse(isoDate));
    }

}
