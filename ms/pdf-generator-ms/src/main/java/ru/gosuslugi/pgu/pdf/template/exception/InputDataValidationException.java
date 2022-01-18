package ru.gosuslugi.pgu.pdf.template.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InputDataValidationException extends PdfGenerateException {

    public InputDataValidationException(String message) {
        super(message);
    }

    public InputDataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
