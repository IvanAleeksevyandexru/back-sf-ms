package ru.gosuslugi.pgu.draftconverter.validation.exception;

import ru.gosuslugi.pgu.common.core.exception.PguException;

/**
 * Выбрасывается при неудачной валидации JSON.
 */
public class JsonValidationException extends PguException {

    public JsonValidationException(String message){
        super(message);
    }

    public JsonValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
