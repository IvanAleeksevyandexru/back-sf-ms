package ru.gosuslugi.pgu.smevconverter.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.client.draftconverter.DraftConverterClient;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.dto.SmevConverterPullRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterGetRequestDto;
import ru.gosuslugi.pgu.dto.SmevConverterPushRequestDto;
import ru.gosuslugi.pgu.dto.XmlCustomConvertRequest;
import ru.gosuslugi.pgu.smevconverter.client.SmevClient;
import ru.gosuslugi.pgu.smevconverter.model.SmevPullResponseDto;
import ru.gosuslugi.pgu.smevconverter.model.SmevServiceResponseDto;

import java.util.Map;

@Service
@AllArgsConstructor
public class SmevConverterServiceImpl implements SmevConverterService {

    private final SmevClient smevClient;
    private final DraftConverterClient draftConverterClient;

    @Override
    public Map<Object, Object> get(SmevConverterGetRequestDto request) {

        var response = smevClient.get(request.getData());

        var xmlCustomConvertRequest = new XmlCustomConvertRequest(response.getData(), request.getServiceId(), request.getTemplateName(), request.getExtData());
        return draftConverterClient.convertXmlCustom(xmlCustomConvertRequest);
    }

    @Override
    public String push(SmevConverterPushRequestDto request) {
        return smevClient.push(request.getData());
    }

    @Override
    public SmevPullResponseDto pull(SmevConverterPullRequestDto request) {
        var pullResponse = smevClient.pull(request.getRequestId());
        if (HttpStatus.OK == pullResponse.getStatus()) {
            var response = JsonProcessingUtil.getObjectMapper().convertValue(pullResponse.getResponse(), SmevServiceResponseDto.class);
            var xmlCustomConvertRequest = new XmlCustomConvertRequest(response.getData(), request.getServiceId(), request.getTemplateName(), request.getExtData());
            pullResponse.setResponse(draftConverterClient.convertXmlCustom(xmlCustomConvertRequest));
        }
        return pullResponse;
    }
}
