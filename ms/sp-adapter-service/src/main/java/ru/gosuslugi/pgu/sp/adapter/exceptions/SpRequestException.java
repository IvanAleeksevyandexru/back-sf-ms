package ru.gosuslugi.pgu.sp.adapter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.gosuslugi.pgu.common.core.exception.PguException;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SpRequestException extends PguException {
    private SpRequestErrorDto spRequestError;

    public SpRequestException(String message, SpRequestErrorDto spRequestError, Throwable cause) {
        super(message, cause);
        this.spRequestError = spRequestError;
    }

    public SpRequestErrorDto getSpRequestError() {
        return spRequestError;
    }
}
