package ru.gosuslugi.pgu.generator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest;
import ru.gosuslugi.pgu.generator.service.fines.AppealFinesService;

import javax.validation.Valid;

/**
 * Controller that handle all methods for scenarios
 * Next/prev pages
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/scenario/fines", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AppealFinesController {

    private final AppealFinesService appealFinesService;

    @PostMapping
    public void generateScenario(@RequestBody @Valid AppealFinesRequest request) {
        log.info("Generation started for request {}", request);

        appealFinesService.generateMainService(request);
    }

    @PostMapping("/debug")
    public ServiceDescriptor generateScenarioWithDebug(@RequestBody @Valid AppealFinesRequest request) {
        log.info("DEBUG Generation started for request {}", request);

        return appealFinesService.generateMainService(request);
    }

    @PostMapping("/additional")
    public void additionalSteps(@RequestParam String serviceId) {
        log.info("Started generation of additional step for Appeal Fines. ServiceId: {}", serviceId);

        appealFinesService.generateAdditionalSteps(serviceId);
    }

    @PostMapping("/additional/debug")
    public ServiceDescriptor additionalStepsWithDebug(@RequestParam String serviceId) {
        log.info("DEBUG Started generation of additional step for Appeal Fines. ServiceId: {}", serviceId);

        return appealFinesService.generateAdditionalSteps(serviceId);
    }

}
