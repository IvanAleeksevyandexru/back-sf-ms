package ru.gosuslugi.pgu.sp.adapter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gosuslugi.pgu.common.core.exception.PguException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SpAdapterServiceException extends PguException {

    public SpAdapterServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpAdapterServiceException(String message) {
        super(message);
    }

}
