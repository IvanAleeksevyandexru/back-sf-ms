package ru.gosuslugi.pgu.pdf.template.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DefaultPDFGenerationException extends PdfGenerateException {

    public DefaultPDFGenerationException(String message) {
        super(message);
    }

    public DefaultPDFGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
