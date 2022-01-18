package ru.gosuslugi.pgu.service.descriptor.storage.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.ServiceDescriptorRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.TemplatePackageRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.DbServiceDescriptor;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SpParamsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class SpParamsServiceTest {

    @Autowired
    private SpParamsService spParamsService;

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
    }

    @Test
    public void testGetSpParams() {
        String spConfig = spParamsService.get("10000000100");
        assert "{\"param1\":\"Param1Value\",\"param2\":\"Param2Value\"}".equals(spConfig);
    }

}
