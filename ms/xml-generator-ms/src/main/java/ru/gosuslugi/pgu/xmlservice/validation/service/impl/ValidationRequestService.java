package ru.gosuslugi.pgu.xmlservice.validation.service.impl;

import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;
import ru.gosuslugi.pgu.xmlservice.validation.exception.ValidationException;
import ru.gosuslugi.pgu.xmlservice.validation.service.ValidationService;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Проверяет запрос на формирование файла XML.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ValidationRequestService implements ValidationService<GenerateXmlRequest> {
    private static final Set<FileType> ALLOWED_FILE_TYPES =
            Stream.of(FileType.XML, FileType.REQUEST).collect(Collectors.toUnmodifiableSet());
    private static final String ALLOWED_FILE_TYPE_NAMES = ALLOWED_FILE_TYPES.stream()
            .map(FileType::getValue).collect(Collectors.joining(", "));

    @Override
    public void validate(GenerateXmlRequest request) {
        FileDescription fileDesc = request.getFileDescription();
        if (Objects.isNull(fileDesc)) {
            return;
        }
        checkFileType(fileDesc);
        checkTemplateName(fileDesc, request.getRoleId());
    }

    private void checkTemplateName(FileDescription fileDesc, String roleId) {
        final Map<ApplicantRole, String> templates = fileDesc.getTemplates();
        if (Objects.isNull(templates)) {
            throw new ValidationException("В запросе не указано описание формируемого файла.");
        }
        if (StringUtils.isEmpty(templates.get(ApplicantRole.valueOf(roleId)))) {
            throw new ValidationException(String.format("В описании файла для роли  '%s'"
                    + " не определено имя шаблона.", roleId));
        }
    }

    private void checkFileType(FileDescription fileDesc) {
        final FileType type = fileDesc.getType();
        if (!ALLOWED_FILE_TYPES.contains(type)) {
            throw new ValidationException(String.format("Запрос на формирование файла содержит "
                            + "некорректный тип файла: %s. Ожидается один из следующих типов: %s",
                    type, ALLOWED_FILE_TYPE_NAMES));
        }
    }
}
