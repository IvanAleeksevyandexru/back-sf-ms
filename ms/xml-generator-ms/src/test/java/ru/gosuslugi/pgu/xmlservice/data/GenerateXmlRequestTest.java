package ru.gosuslugi.pgu.xmlservice.data;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Проверяет валидацию запроса на формирование файлов.
 */
public class GenerateXmlRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static void assertContains(Set<ConstraintViolation<GenerateXmlRequest>> violations,
            String propPath, Class<? extends Annotation> annotationType) {
        Assert.assertTrue(violations.stream()
                .anyMatch((v -> Objects.equals(v.getPropertyPath().toString(), propPath)
                        && annotationType.isAssignableFrom(
                        v.getConstraintDescriptor().getAnnotation().annotationType()))));
    }

    private static <T> void assumeThat(T actual, Matcher<? super T> matcher) {
        if (!matcher.matches(actual)) {
            throw new SkipException("");
        }
    }

    @Test
    public void shouldPassWhenValidDtoGiven() {
        // given
        GenerateXmlRequest request = createInitialRequest();

        // when
        Set<ConstraintViolation<GenerateXmlRequest>> violations = validator.validate(request);

        // then
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldFailWhenFileDescriptionNotProvided() {
        // given
        GenerateXmlRequest request = new GenerateXmlRequest("1", 1L, 2L, "role", null);

        // when
        assumeThat(request.getFileDescription(), is(nullValue()));
        Set<ConstraintViolation<GenerateXmlRequest>> violations =
                validator.validate(request);

        // then
        assertContains(violations, "fileDescription", NotNull.class);
    }

    @Test
    public void shouldFailWhenServiceIdIsBlank() {
        // given
        GenerateXmlRequest request = new GenerateXmlRequest("", 1L, 2L, "role",
                new FileDescription());

        // when
        Set<ConstraintViolation<GenerateXmlRequest>> nonGroupViolations =
                validator.validate(request);

        // then
        assertContains(nonGroupViolations, "serviceId", NotBlank.class);
    }

    private GenerateXmlRequest createInitialRequest() {
        return new GenerateXmlRequest("1", 1L, 2L, "role", new FileDescription());
    }
}
