package ru.gosuslugi.pgu.smevconverter.service;

import ru.gosuslugi.pgu.dto.SmevConverterPullRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterGetRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPushRequestDto;
import ru.gosuslugi.pgu.smevconverter.model.SmevPullResponseDto;

import java.util.Map;

public interface SmevConverterService {

    Map<Object, Object> get(SmevConverterGetRequestDto request);
    String push(SmevConverterPushRequestDto request);
    SmevPullResponseDto pull(SmevConverterPullRequestDto request);
}
