package ru.gosuslugi.pgu.smevconverter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmevServiceResponseDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("ttl")
    private Integer ttl;

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private String data;
}
