package ru.gosuslugi.pgu.smevconverter.model;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
@Getter
public class BarbarbokPushResponseDto {

    private String id;
}