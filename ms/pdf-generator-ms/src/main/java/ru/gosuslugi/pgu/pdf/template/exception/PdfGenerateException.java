package ru.gosuslugi.pgu.pdf.template.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gosuslugi.pgu.common.core.exception.PguException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PdfGenerateException extends PguException {
    public PdfGenerateException(String message) {
        super(message);
    }

    public PdfGenerateException(String message, Throwable cause) {
        super(message, cause);
    }
}
