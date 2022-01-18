package ru.gosuslugi.pgu.xmlservice.validation.service.impl;

import ru.gosuslugi.pgu.xmlservice.validation.exception.ValidationException;
import ru.gosuslugi.pgu.xmlservice.validation.service.ValidationService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Slf4j
@Service
@AllArgsConstructor
public class ValidationXmlService implements ValidationService<String> {

    private final DocumentBuilderFactory factory;

    @Override
    public void validate(String fileContent) {
        if (StringUtils.isBlank(fileContent)) {
            return;
        }
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            try (ByteArrayInputStream is = new ByteArrayInputStream(fileContent.getBytes())) {
                builder.parse(is);
            }
        } catch (ParserConfigurationException | IOException e) {
            throw new ValidationException(
                    "Не удалось произвести валидацию результирующего XML-файла: "
                            + e.getMessage(), e);
        } catch (SAXException e) {
            log.error("В XML структуре найдена ошибка: {}. Ошибочное содержимое: '{}'",
                    e.getMessage(), fileContent);
            throw new ValidationException(e);
        }
    }
}
