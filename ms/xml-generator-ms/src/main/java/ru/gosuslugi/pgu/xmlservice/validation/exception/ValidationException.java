package ru.gosuslugi.pgu.xmlservice.validation.exception;

import ru.gosuslugi.pgu.fs.common.exception.FormBaseException;

/**
 * Ошибка валидации XML.
 */
public class ValidationException extends FormBaseException {
    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String s) {
        super(s);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
