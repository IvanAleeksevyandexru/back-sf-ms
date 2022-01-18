package ru.gosuslugi.pgu.service.descriptor.storage.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Root of project exceptions
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ServiceDescriptorException extends RuntimeException {

    /**
     * Used for return dynamic http status. If empty used status from annotation @ResponseStatus
     */
    @Getter
    @Setter
    private HttpStatus status = null;

    public ServiceDescriptorException() {
        super();
    }

    public ServiceDescriptorException(String message) {
        super(message);
    }

    public ServiceDescriptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public boolean isWorkflowException() {
        return false;
    }
}
