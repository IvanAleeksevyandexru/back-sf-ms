package ru.gosuslugi.pgu.service.descriptor.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.TemplatePackage;
import ru.gosuslugi.pgu.service.descriptor.storage.service.TemplatePackageService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/templates", consumes = {MediaType.APPLICATION_JSON_VALUE})
public class TemplatePackageController {

    private final TemplatePackageService service;

    @GetMapping("/{serviceId}")
    public ResponseEntity<ByteBuffer> get(@PathVariable("serviceId") String serviceId) {
        return ResponseEntity.ok(service.get(serviceId).getPackageFile());
    }

    @PutMapping(value = "/{serviceId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Instant> create(@PathVariable("serviceId") String serviceId, @RequestParam("template") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.save(serviceId, ByteBuffer.wrap(file.getBytes())));
    }

    @GetMapping("/{serviceId}/refresh")
    public ResponseEntity<Instant> refresh(@PathVariable("serviceId") String serviceId) {
        return ResponseEntity.ok(service.refresh(serviceId).getUpdated());
    }

    @PostMapping("/checksums")
    public ResponseEntity<Map<String, String>> checksums(@RequestBody List<String> serviceIds) {
        List<TemplatePackage> templatePackages = service.getServiceTemplateChecksums(serviceIds);
        Map<String, String> checksums = new HashMap<>(templatePackages.size() + 10);
        for (TemplatePackage templatePackage : templatePackages) {
            checksums.put(templatePackage.getServiceId(), templatePackage.getChecksum());
        }
        return ResponseEntity.ok(checksums);
    }

}
