package ru.gosuslugi.pgu.service.descriptor.storage.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.ServiceDescriptorRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.TemplatePackageRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.DbServiceDescriptor;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.TemplatePackage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractMockRepositoryTest {

    @MockBean
    ServiceDescriptorRepository serviceDescriptorRepository;

    @MockBean
    TemplatePackageRepository templatePackageRepository;

    @BeforeEach
    public void init() throws IOException {

        byte[] data = this.getClass().getClassLoader().getResourceAsStream("10000000100.json").readAllBytes();
        String jsonString = new String(data, StandardCharsets.UTF_8);
        DbServiceDescriptor dbServiceDescriptor = new DbServiceDescriptor("10000000100", Instant.now(), jsonString);
        Optional<DbServiceDescriptor> dbServiceDescriptorOptional = Optional.of(dbServiceDescriptor);
        when(serviceDescriptorRepository.findById("10000000100")).thenReturn(dbServiceDescriptorOptional);

        byte[] dataTemplate = this.getClass().getClassLoader().getResourceAsStream("package_10000000100.zip").readAllBytes();
        ByteBuffer buffer = ByteBuffer.wrap(dataTemplate);
        TemplatePackage initialTemplatePackage = new TemplatePackage("10000000100", Instant.now(), buffer, "");
        Optional<TemplatePackage> initialTemplatePackageOptional = Optional.of(initialTemplatePackage);
        when(templatePackageRepository.findById("10000000100")).thenReturn(initialTemplatePackageOptional);

    }

}
