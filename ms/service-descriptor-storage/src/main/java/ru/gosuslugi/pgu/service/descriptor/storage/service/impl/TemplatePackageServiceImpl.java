package ru.gosuslugi.pgu.service.descriptor.storage.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.TemplatePackageRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.TemplatePackage;
import ru.gosuslugi.pgu.service.descriptor.storage.service.TemplatePackageService;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplatePackageServiceImpl implements TemplatePackageService {

    private final TemplatePackageRepository repository;

    public TemplatePackage get(String serviceId) {
        return repository.findById(serviceId).orElseThrow();
    }

    @SneakyThrows
    public Instant save(String serviceId, ByteBuffer body) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(body.array());
        byte[] digest = md.digest();
        String checksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
        TemplatePackage saved = repository.save(new TemplatePackage(serviceId, Instant.now(), body, checksum));
        return saved.getUpdated();
    }

    public TemplatePackage refresh(String serviceId) {
        TemplatePackage templatePackage = repository.findById(serviceId).orElseThrow();
        if (Objects.nonNull(templatePackage)) {
            return templatePackage;
        }
        return new TemplatePackage(null, Instant.EPOCH, null, null);
    }

    public List<TemplatePackage> getServiceTemplateChecksums(List<String> serviceIds) {
        return repository.findAllById(serviceIds);
    }
}
