package ru.gosuslugi.pgu.identification.luna.dto.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {

    private Integer status;
    private String filename;
    private LunaError error;

}
