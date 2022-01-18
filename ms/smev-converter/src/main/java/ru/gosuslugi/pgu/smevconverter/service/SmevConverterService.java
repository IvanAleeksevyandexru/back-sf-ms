package ru.gosuslugi.pgu.smevconverter.service;

import ru.gosuslugi.pgu.dto.SmevConverterRequestDto;

import java.util.Map;

public interface SmevConverterService {

    Map<Object, Object> get(SmevConverterRequestDto request);
}
