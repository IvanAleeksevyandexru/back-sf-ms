package ru.gosuslugi.pgu.service.descriptor.storage.test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SpConfigService;
import ru.gosuslugi.pgu.service.descriptor.storage.test.AbstractMockRepositoryTest;
import ru.gosuslugi.pgu.service.descriptor.storage.test.model.DescriptorForTests;

public class SpConfigServiceTest extends AbstractMockRepositoryTest {

    @Autowired
    private SpConfigService spConfigService;

    @Test
    public void testGetSpConfig() throws JsonProcessingException {
        String spConfig = spConfigService.get("10000000100");
        ObjectMapper objectMapper = new ObjectMapper();
        DescriptorForTests desc = objectMapper.readValue(spConfig, DescriptorForTests.class);
        assert desc.getBusinessXmlName().equals("attach.xml");
        assert desc.getReplacedHeaders().size() == 2;
        assert desc.getAlwaysAttachServicePdf();
        assert desc.getServiceCustomId().equals("05040302");
    }

}
