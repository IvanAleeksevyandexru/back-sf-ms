package ru.gosuslugi.pgu.identification.core.api.dto;

import lombok.Data;

@Data
public class ImageQuality {

    private Double blurriness;
    private Double dark;
    private Double illumination;
    private Double specularity;
    private Double light;

}
