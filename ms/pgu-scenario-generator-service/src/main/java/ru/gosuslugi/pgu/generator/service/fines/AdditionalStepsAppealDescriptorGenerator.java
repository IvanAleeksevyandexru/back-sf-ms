package ru.gosuslugi.pgu.generator.service.fines;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.generator.model.ReasonTemplate;
import ru.gosuslugi.pgu.generator.model.appeal.cycle.AppealCycleResponse;
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest;
import ru.gosuslugi.pgu.generator.model.scenario.EvidenceTemplateType;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.gosuslugi.pgu.generator.util.DescriptorUtils.*;
import static ru.gosuslugi.pgu.generator.util.SourceXmlUtils.getReasonForAdditionalSteps;

@Component
@RequiredArgsConstructor
public class AdditionalStepsAppealDescriptorGenerator {

    private final AppealDescriptorGenerator appealDescriptorGenerator;

    public ServiceDescriptor addAdditionalStepsToService(ServiceDescriptor serviceDescriptor,
                                            AppealCycleResponse additionalDocsRequest,
                                            AppealFinesRequest request) {
        clearPreviousSteps(serviceDescriptor);

        createAndAddAdditionStartScreen(serviceDescriptor, additionalDocsRequest, request);

        ReasonTemplate reason = getReasonForAdditionalSteps(additionalDocsRequest);

        List<String> parentScreenIds = List.of(ADDITIONAL_START_SCREEN_NAME);

        int evidenceScreenCount = 0;
        for (List<EvidenceTemplateType> evidencePage : reason.getEvidencePages()) {
            evidenceScreenCount++;
            String screenId = ADDITIONAL_STEP_SCREEN_PREFIX + evidenceScreenCount;

            appealDescriptorGenerator.createAndAddEvidenceScreen(serviceDescriptor, "Дополнительные сведения",
                    reason, evidenceScreenCount, evidencePage, screenId);

            addRuleToParentScreen(serviceDescriptor, null, parentScreenIds, screenId, false);

            parentScreenIds = List.of(screenId);
        }
        addRuleToParentScreen(serviceDescriptor, null, parentScreenIds, ADDITIONAL_FINISH_SCREEN_NAME, false);

        createAndAddAdditionFinishScreen(serviceDescriptor);

        return serviceDescriptor;
    }

    private void clearPreviousSteps(ServiceDescriptor serviceDescriptor) {
        Set<String> componentsToDelete = serviceDescriptor.getScreens().stream()
                .filter(screen -> screen.getId().startsWith(ADDITIONAL_STEP_SCREEN_PREFIX))
                .flatMap(screen -> screen.getComponentIds().stream())
                .collect(Collectors.toSet());

        serviceDescriptor.getScreens().removeIf(screen -> screen.getId().startsWith(ADDITIONAL_STEP_SCREEN_PREFIX));
        serviceDescriptor.getScreenRules().keySet().removeIf(screen -> screen.startsWith(ADDITIONAL_STEP_SCREEN_PREFIX));
        serviceDescriptor.getApplicationFields().removeIf(component -> componentsToDelete.contains(component.getId()));

        serviceDescriptor.getParameters().put("Id", UUID.randomUUID().toString());
    }


}
