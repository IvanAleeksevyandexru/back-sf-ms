package ru.gosuslugi.pgu.generator.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.generator.client.dto.IpshRequestDto;
import ru.gosuslugi.pgu.generator.client.dto.IpshStatusDto;
import ru.gosuslugi.pgu.generator.config.properties.IpshClientProperties;
import ru.gosuslugi.pgu.generator.exception.DepartmentExchangeException;
import ru.gosuslugi.pgu.generator.model.appeal.scenario.GetAppealScenarioResponse;
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest;
import ru.gosuslugi.pgu.generator.service.XmlUnmarshallService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.debug;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpshRestClient {

    private final static String REQUEST_URL = "/offence/protocol/request?billNumber={billNumber}&routeNumber={routeNumber}&forceScenarioUpdate=true&doNotSendDocument=true";
    private final static String STATUS_URL = "/offence/protocol/status?requestId={requestId}&withScenarioXml=true&doNotSendDocument=true";
    private static final List<Integer> POSITIVE_IPSH_RESPONSE_CODES = List.of(0, 95, 399);

    private final IpshClientProperties properties;
    private final RestTemplate restTemplate;
    private final XmlUnmarshallService xmlUnmarshallService;

    public GetAppealScenarioResponse getFinesAppealXml(AppealFinesRequest request) {

        String xmlFromDepartment = requestXmlFromDepartment(request);

        return xmlUnmarshallService.unmarshal(xmlFromDepartment, GetAppealScenarioResponse.class);
    }

    private String requestXmlFromDepartment(AppealFinesRequest request) {
        String requestId = sendRequest(request);

        for (int i = 0; i < properties.getMaxStatusRequestCount(); i++) {
            sleep(properties.getMaxStatusRequestTimeoutMs());
            IpshStatusDto statusDto = getStatus(requestId, request.getToken());
            if (statusDto.getError().getCode() == 0) {
                if (statusDto.getResponse() == null || StringUtils.isEmpty(statusDto.getResponse().getScenario())) {
                    throw new ExternalServiceException("Scenario xml not found in status response: " + statusDto);
                }
                return statusDto.getResponse().getScenario();
            }
        }
        throw new ExternalServiceException("Scenario XML request timeout");
    }

    private String sendRequest(AppealFinesRequest request) {
        debug(log, () -> String.format("Request xml from IPSH for BillNumber %s", request.getBillNumber()));
        ResponseEntity<IpshRequestDto> response;
        try {
            response = restTemplate
                    .exchange(properties.getUrl() + REQUEST_URL,
                            HttpMethod.GET,
                            new HttpEntity<String>(getHeaders(request.getToken())),
                            IpshRequestDto.class,
                            Map.of("billNumber", request.getBillNumber(),
                                    "routeNumber", request.getRouteNumber())
                    );
        } catch (RestClientException e) {
            throw new ExternalServiceException(e);
        }

        if (response.getBody() == null) {
            throw new ExternalServiceException("Ошибка отправки запроса в ведомство. Пустое тело ответа.");
        }
        int ipshCode = response.getBody().getError().getCode();
        if (!POSITIVE_IPSH_RESPONSE_CODES.contains(ipshCode)) {
            throw new DepartmentExchangeException(ipshCode,
                    "Ошибка отправки запроса в ведомство. Body: " + response.getBody());
        }
        return response.getBody().getResponse().getRequestId();
    }

    private IpshStatusDto getStatus(String requestId, String token) {
        debug(log, () -> String.format("Request status from IPSH for requestId %s", requestId));
        ResponseEntity<IpshStatusDto> response;
        try {
            response = restTemplate
                    .exchange(properties.getUrl() + STATUS_URL,
                            HttpMethod.GET,
                            new HttpEntity<String>(getHeaders(token)),
                            IpshStatusDto.class,
                            Map.of("requestId", requestId)
                    );
        } catch (RestClientException e) {
            throw new ExternalServiceException(e);
        }

        if (response.getBody() == null) {
            throw new ExternalServiceException("Ошибка запроса статуса об отправке в ведомство. Пустое тело ответа.");
        }
        if (response.getBody().getError().getCode() > 1) {
            throw new DepartmentExchangeException(getDepartmentErrorCode(response.getBody()),
                    "Ошибка запроса статуса об отправке в ведомство. Body: " + response.getBody());
        }
        return response.getBody();
    }

    private int getDepartmentErrorCode(IpshStatusDto body) {
        return body.getDetailedError() != null ? body.getDetailedError().getCode() : body.getError().getCode();
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Cookie", "acc_t=" + token);
        return httpHeaders;
    }

    private void sleep(int maxStatusRequestTimeoutMs) {
        try {
            TimeUnit.MILLISECONDS.sleep(maxStatusRequestTimeoutMs);
        } catch (InterruptedException e) {
            log.debug("Thread interrupted", e);
        }
    }
}
