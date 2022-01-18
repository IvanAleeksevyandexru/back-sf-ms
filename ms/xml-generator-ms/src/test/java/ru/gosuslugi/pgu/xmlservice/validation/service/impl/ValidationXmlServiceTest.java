package ru.gosuslugi.pgu.xmlservice.validation.service.impl;

import ru.gosuslugi.pgu.xmlservice.validation.exception.ValidationException;
import ru.gosuslugi.pgu.xmlservice.validation.service.ValidationService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ValidationXmlServiceTest {

    @Autowired
    private ValidationService<String> validationXmlService;

    @Test
    public void validateEmptyXmlTest() {
        validationXmlService.validate("");
    }

    @Test
    public void validateNullXmlTest() throws ValidationException {
        validationXmlService.validate(null);
    }

    @Test
    public void validateWrongXmlTest() {
        Assertions.assertThrows(ValidationException.class,
                () -> validationXmlService.validate("<a>Hi!</b>"));
    }

    @Test
    public void validateRightXmlTest() throws ValidationException {
        validationXmlService.validate("<a>This is right XML</a>");
    }
}
