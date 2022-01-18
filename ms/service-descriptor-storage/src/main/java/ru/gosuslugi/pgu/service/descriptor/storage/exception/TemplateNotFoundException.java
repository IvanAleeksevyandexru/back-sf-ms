package ru.gosuslugi.pgu.service.descriptor.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gosuslugi.pgu.common.core.exception.PguException;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Шаблон не найден")
public class TemplateNotFoundException extends PguException {
    public TemplateNotFoundException() {
        super("Шаблон не найден");
    }
}
