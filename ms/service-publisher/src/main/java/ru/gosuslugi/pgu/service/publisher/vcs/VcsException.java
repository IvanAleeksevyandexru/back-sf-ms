package ru.gosuslugi.pgu.service.publisher.vcs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VcsException extends RuntimeException {

    public VcsException(String s) {
        super(s);
    }

    public VcsException(String message, Throwable cause) {
        super(message, cause);
    }

    public VcsException(Throwable cause) {
        super(cause);
    }
}
