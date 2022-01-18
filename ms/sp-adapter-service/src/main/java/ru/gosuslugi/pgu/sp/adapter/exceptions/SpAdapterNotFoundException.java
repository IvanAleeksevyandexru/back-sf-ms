package ru.gosuslugi.pgu.sp.adapter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gosuslugi.pgu.common.core.exception.PguException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SpAdapterNotFoundException extends PguException {

    public SpAdapterNotFoundException(String message) {
        super(message);
    }

}
