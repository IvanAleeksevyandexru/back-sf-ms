package ru.gosuslugi.pgu.service.descriptor.storage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gosuslugi.pgu.common.core.exception.PguException;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Пакет шаблонов сервиса повреждён")
public class WrongArchiveException extends PguException {
    public WrongArchiveException(Throwable cause){
        super(cause);
    }
}
