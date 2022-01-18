package ru.gosuslugi.pgu.generator.client.dto;

import lombok.Data;

@Data
public class BaseResponse {

    private Error error;

    @Data
    public static class Error {

        private int code;

        private String message;

    }

}
