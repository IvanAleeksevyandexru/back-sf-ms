package ru.gosuslugi.pgu.identification.luna.dto.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetectionQuality {

    private Double blurriness;
    private Double dark;
    private Double illumination;
    private Double specularity;
    private Double light;

}
