package ru.gosuslugi.pgu.identification.core.model;

import lombok.Data;

@Data
public class PassportIdentification {

    private String faceId;

    private Object allParams;
}
