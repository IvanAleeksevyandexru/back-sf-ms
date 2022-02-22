package ru.gosuslugi.pgu.smevconverter.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
@Getter
public class BarbarbokResponseDto {

    private String data;
    private ErrorDto smev3CallError;

    public boolean hasError() {
        return smev3CallError != null;
    }

    public String getErrorMessage() {
        return hasError() ? smev3CallError.getMessage() : null;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    @NoArgsConstructor
    @Getter
    static class ErrorDto {
        private String errorCode;
        private String message;
    }
}