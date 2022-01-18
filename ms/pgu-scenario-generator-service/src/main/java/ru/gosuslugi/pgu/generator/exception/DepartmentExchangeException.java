package ru.gosuslugi.pgu.generator.exception;

import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;

public class DepartmentExchangeException extends ExternalServiceException {

    private final int errorCode;

    public DepartmentExchangeException(int errorCode, String s) {
        super(s);
        this.errorCode = errorCode;
    }

    @Override
    public Object getValue() {
        return errorCode;
    }
}
