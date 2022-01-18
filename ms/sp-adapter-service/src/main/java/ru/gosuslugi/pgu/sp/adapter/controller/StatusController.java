package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.sp.adapter.types.PackageProcessingStatus;

import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping(value = "/templates", produces = MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class StatusController {

    private final Map<String, PackageProcessingStatus> packageProcessingStatusMap;

    @GetMapping(value = "/processing-status")
    public Collection<PackageProcessingStatus> getTemplatesProcessingStatus() {
        return packageProcessingStatusMap.values();
    }

    @GetMapping(value = "/processing-status/{serviceId}")
    public PackageProcessingStatus getServiceTemplateProcessingStatus(@PathVariable String serviceId) {
        return packageProcessingStatusMap.getOrDefault(serviceId,
                PackageProcessingStatus.builder()
                        .serviceId(serviceId)
                        .statusDescription("service not found")
                        .build()
        );
    }

}
