package ru.gosuslugi.pgu.smevconverter.service;

import org.springframework.http.ResponseEntity;
import ru.gosuslugi.pgu.dto.SmevConverterGetRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPullRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPushRequestDto;

import java.util.Map;

public interface SmevConverterService {

    ResponseEntity<Map<Object, Object>> get(SmevConverterGetRequestDto request);
    ResponseEntity<Map<Object, Object>> push(SmevConverterPushRequestDto request);
    ResponseEntity<Map<Object, Object>> pull(SmevConverterPullRequestDto request);
}
