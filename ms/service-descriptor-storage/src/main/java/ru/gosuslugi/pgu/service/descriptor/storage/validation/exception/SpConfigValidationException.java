package ru.gosuslugi.pgu.service.descriptor.storage.validation.exception;

public class SpConfigValidationException extends Exception{

    public SpConfigValidationException(String message,Exception e){
        super(message, e);
    }

    public SpConfigValidationException(String message){
        super(message);
    }

}
