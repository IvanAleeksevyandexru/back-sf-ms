package ru.gosuslugi.pgu.xmlservice.validation.service.impl;

import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;
import ru.gosuslugi.pgu.xmlservice.validation.exception.ValidationException;
import ru.gosuslugi.pgu.xmlservice.validation.service.ValidationService;

import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ValidationRequestServiceTest {
    private static final ApplicantRole ROLE = ApplicantRole.Applicant;
    private static final String ROLE_NAME = ROLE.name();
    private static final String TEMPLATE_FILE_NAME = "template.vm";
    private ValidationService<GenerateXmlRequest> sut;

    @BeforeMethod
    public void setUp() {
        sut = new ValidationRequestService();
    }

    @Test
    public void shouldPassWhenFileDescriptionIsNull() {
        // given
        GenerateXmlRequest request = new GenerateXmlRequest("1", 2L, 3L, ROLE_NAME, null);

        // when
        sut.validate(request);

        // then pass
    }

    @Test
    public void shouldPassWhenFileDescriptionValid() {
        // given
        GenerateXmlRequest request =
                new GenerateXmlRequest("1", 2L, 3L, ROLE_NAME, getValidDescription());

        // when
        sut.validate(request);

        // then pass
    }

    @Test
    public void shouldFailWhenFileTypeInvalid() {
        // given
        FileDescription desc = getValidDescription();
        desc.setType(FileType.PDF);
        GenerateXmlRequest request = new GenerateXmlRequest("1", 2L, 3L, ROLE_NAME, desc);

        // when
        Assert.assertThrows(ValidationException.class, () -> sut.validate(request));

        // then pass
    }

    @Test
    public void shouldFailWhenTemplateNameForRoleUndefined() {
        // given
        FileDescription desc = getValidDescription();
        desc.setTemplates(Collections.emptyMap());
        GenerateXmlRequest request = new GenerateXmlRequest("1", 2L, 3L, ROLE_NAME, desc);

        // when
        Assert.assertThrows(ValidationException.class, () -> sut.validate(request));

        // then pass
    }

    private FileDescription getValidDescription() {
        final FileDescription fileDescription = new FileDescription();
        fileDescription.setType(FileType.XML);
        fileDescription.setTemplates(Map.of(ROLE, TEMPLATE_FILE_NAME));
        return fileDescription;
    }
}
