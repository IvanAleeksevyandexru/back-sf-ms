package ru.gosuslugi.pgu.service.descriptor.storage.test.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SingleTemplateService;
import ru.gosuslugi.pgu.service.descriptor.storage.test.AbstractMockRepositoryTest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SingleTemplateServiceTest extends AbstractMockRepositoryTest {

    @Autowired
    SingleTemplateService singleTemplateService;

    /**
     * Проверка на то, что сервис достаёт из зазиованные шаблоны и по пути вытаскивает тот, что нужно
     * @throws IOException
     */
    @Test
    public void testTemplateParcing() throws IOException {
        ByteBuffer byteBuffer = singleTemplateService.get("10000000100", "sections/applicant/pdf_10000000100_Applicant_additional_info.vm");
        byte[] data = this.getClass().getClassLoader().getResourceAsStream("pdf_10000000100_Applicant_additional_info.vm").readAllBytes();
        ByteBuffer etalon = ByteBuffer.wrap(data);
        assert StandardCharsets.UTF_8.decode(byteBuffer).toString().replace("\r\n", "\n")
                .equals(StandardCharsets.UTF_8.decode(etalon).toString().replace("\r\n", "\n"));
    }

    @Test
    public void testCrc() throws IOException {
        Long crc = singleTemplateService.getCRC("10000000100", "sections/applicant/pdf_10000000100_Applicant_additional_info.vm");
        assert 3547713431L == crc;
    }

}
