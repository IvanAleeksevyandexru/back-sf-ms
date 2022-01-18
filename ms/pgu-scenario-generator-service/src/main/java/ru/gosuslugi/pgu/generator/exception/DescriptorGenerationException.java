package ru.gosuslugi.pgu.generator.exception;

import ru.gosuslugi.pgu.common.core.exception.PguException;

public class DescriptorGenerationException extends PguException {

    public DescriptorGenerationException(String message) {
        super(message);
    }

    public DescriptorGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
