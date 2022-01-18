package ru.gosuslugi.pgu.xmlservice.exception;

import ru.gosuslugi.pgu.common.core.exception.handler.ExceptionHandlerHelper;
import ru.gosuslugi.pgu.common.core.exception.handler.GlobalExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Перехватывает исключения микросервиса для обеспечения корректного вывода ошибки и логирования.
 */
@RestControllerAdvice
public class ServiceExceptionHandler extends GlobalExceptionHandler {
    public ServiceExceptionHandler(ExceptionHandlerHelper handlerHelper) {
        super(handlerHelper);
    }
}
