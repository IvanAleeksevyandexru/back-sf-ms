package ru.gosuslugi.pgu.pdf.template.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Критическая ошибка генерации PDF, которую не нужно скрывать (как прочие).
 * Ошибки с кодом HttpStatus.NOT_ACCEPTABLE и HttpStatus.INTERNAL_SERVER_ERROR не скрываются в SP Adapter
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class CriticalPdfGenerationException extends PdfGenerateException {

    public CriticalPdfGenerationException(String message) {
        super(message);
    }

    public CriticalPdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
