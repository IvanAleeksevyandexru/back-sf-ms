package ru.gosuslugi.pgu.smevconverter.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.smevconverter.config.SmevClientProperties;
import ru.gosuslugi.pgu.smevconverter.model.SmevServiceRequestDto;
import ru.gosuslugi.pgu.smevconverter.model.SmevServiceResponseDto;

import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;

@Slf4j
@RequiredArgsConstructor
public class SmevClientImpl implements SmevClient {

    private final RestTemplate restTemplate;
    private final SmevClientProperties properties;

    @Override
    public SmevServiceResponseDto get(String requestXml) {

        var requestDto = new SmevServiceRequestDto(requestXml, properties.getSmevVersion(), properties.getTimeout(), properties.getTtl());
        try {
            // todo: проверить что приходит по сообщениям
            return restTemplate.postForObject(properties.getUrl() + "/barbarbok/v1/get",
                    new HttpEntity<>(requestDto),
                    SmevServiceResponseDto.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (REQUEST_TIMEOUT.equals(e.getStatusCode())) {
                log.error("Превышение времени ожидания от барбарбок: " + e.getMessage() + " => " + requestDto);
                throw new ExternalServiceException("К сожалению, превышено время ожидания ведомства и " +
                        "мы не получили необходимые сведения. Пожалуйста, повторите попытку позже.");
            }
            log.error("Ошибка при обращении в барбарбок: " + e.getMessage() + " => " + requestDto);
            throw new ExternalServiceException("К сожалению, произошла ошибка и мы не получили необходимые сведения " +
                    "из ведомства. Пожалуйста, повторите попытку позже.");
        }
    }
}
