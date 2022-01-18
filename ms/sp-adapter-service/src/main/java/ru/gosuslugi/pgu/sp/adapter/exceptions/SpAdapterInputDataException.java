package ru.gosuslugi.pgu.sp.adapter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gosuslugi.pgu.common.core.exception.PguException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpAdapterInputDataException extends PguException {

    public SpAdapterInputDataException(String message) {
        super(message);
    }

    public SpAdapterInputDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
