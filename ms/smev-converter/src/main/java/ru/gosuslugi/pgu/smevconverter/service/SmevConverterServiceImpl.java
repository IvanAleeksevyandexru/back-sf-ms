package ru.gosuslugi.pgu.smevconverter.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.client.draftconverter.DraftConverterClient;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.dto.SmevConverterGetRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPullRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPushRequestDto;
import ru.gosuslugi.pgu.dto.XmlCustomConvertRequest;
import ru.gosuslugi.pgu.smevconverter.client.SmevClient;
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokResponseDto;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SmevConverterServiceImpl implements SmevConverterService {

    private static final String ERROR_MESSAGE_KEY = "errorMessage";

    private final SmevClient smevClient;
    private final DraftConverterClient draftConverterClient;

    @Override
    public ResponseEntity<Map<Object, Object>> get(@NonNull final SmevConverterGetRequestDto request) {
        var response = smevClient.get(request.getData());

        return processBarbarbokResponse(response, request.getServiceId(), request.getTemplateName(), request.getExtData());
    }

    @Override
    public ResponseEntity<Map<Object, Object>> push(@NonNull final SmevConverterPushRequestDto request) {
        var response = smevClient.push(request.getData());

        if (HttpStatus.OK != response.getStatusCode() || !response.hasBody()) {
            return new ResponseEntity<>(Map.of(ERROR_MESSAGE_KEY, "Ошибка данных"), response.getStatusCode());
        }

        var body = Objects.requireNonNull(response.getBody());
        if (body.getId() == null) {
            return new ResponseEntity<>(Map.of(ERROR_MESSAGE_KEY, "Ошибка барбарбок"), response.getStatusCode());
        }

        return new ResponseEntity<>(Map.of("id", body.getId()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<Object, Object>> pull(@NonNull final SmevConverterPullRequestDto request) {
        var response = smevClient.pull(request.getRequestId());

        if (HttpStatus.ACCEPTED == response.getStatusCode()) {
            return new ResponseEntity<>(Map.of("id", request.getRequestId()), response.getStatusCode());
        }

        return processBarbarbokResponse(response, request.getServiceId(), request.getTemplateName(), request.getExtData());
    }

    private ResponseEntity<Map<Object, Object>> processBarbarbokResponse(ResponseEntity<BarbarbokResponseDto> response,
                                                                         String serviceId,
                                                                         String templateName,
                                                                         String extData) {
        if (HttpStatus.OK != response.getStatusCode() || !response.hasBody()) {
            return new ResponseEntity<>(Collections.emptyMap(), response.getStatusCode());
        }

        var body = Objects.requireNonNull(response.getBody());
        if (body.hasError()) {
            return new ResponseEntity<>(Map.of(ERROR_MESSAGE_KEY, body.getErrorMessage()), response.getStatusCode());
        }

        var convertRequest = new XmlCustomConvertRequest(body.getData(), serviceId, templateName, extData);
        return getDraftConverterResponse(convertRequest);
    }

    private ResponseEntity<Map<Object, Object>> getDraftConverterResponse(XmlCustomConvertRequest convertRequest) {
        try {
            return new ResponseEntity<>(draftConverterClient.convertXmlCustom(convertRequest), HttpStatus.OK);
        } catch (ExternalServiceException e) {
            return new ResponseEntity<>(Map.of(ERROR_MESSAGE_KEY, "Ошибка vm-шаблона"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of(ERROR_MESSAGE_KEY, "Ошибка сервиса конвертации в json"), HttpStatus.BAD_REQUEST);
        }
    }
}