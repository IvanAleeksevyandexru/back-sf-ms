package ru.gosuslugi.pgu.smevconverter.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.client.draftconverter.DraftConverterClient;
import ru.gosuslugi.pgu.dto.SmevConverterRequestDto;
import ru.gosuslugi.pgu.dto.XmlCustomConvertRequest;
import ru.gosuslugi.pgu.smevconverter.client.SmevClient;

import java.util.Map;

@Service
@AllArgsConstructor
public class SmevConverterServiceImpl implements SmevConverterService {

    private final SmevClient smevClient;
    private final DraftConverterClient draftConverterClient;

    @Override
    public Map<Object, Object> get(SmevConverterRequestDto request) {

        var response = smevClient.get(request.getData());

        var xmlCustomConvertRequest = new XmlCustomConvertRequest(response.getData(), request.getServiceId(), request.getTemplateName(), request.getExtData());
        return draftConverterClient.convertXmlCustom(xmlCustomConvertRequest);
    }
}
