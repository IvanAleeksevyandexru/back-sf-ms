package ru.gosuslugi.pgu.generator.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipationEnum {

    REQUIRED("_wp"),
    NOT_REQUIRED("_wop"),
    ASK("");

    private final String suffix;

}
