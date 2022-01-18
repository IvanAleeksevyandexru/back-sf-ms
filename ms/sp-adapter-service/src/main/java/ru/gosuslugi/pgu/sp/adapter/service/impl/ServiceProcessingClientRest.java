package ru.gosuslugi.pgu.sp.adapter.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.atc.carcass.common.ws.JaxWSClientFactoryImpl;
import ru.gosuslugi.pgu.common.esia.search.service.UddiService;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;
import ru.gosuslugi.pgu.sp.adapter.data.SmevRequest;
import ru.gosuslugi.pgu.sp.adapter.dto.AdditionalParametersDto;
import ru.gosuslugi.pgu.sp.adapter.dto.ServiceProcessingDto;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterServiceException;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpRequestException;
import ru.gosuslugi.pgu.sp.adapter.service.ServiceProcessingClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * Клиент для sp (service-precessing)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProcessingClientRest implements ServiceProcessingClient {

    private static final String UDDI_ENDPOINT = "uddi:gosuslugi.ru:services:service-processing";
    private static final String PATH = "/v1/ORDER_CALL/{uriServiceCode}/{orderId}/{token}";

    private final RestTemplate restTemplate;
    private final UddiService uddiService;
    private final ObjectMapper objectMapper;

    public void orderCall(SmevRequest smevRequest) {
        final String requestBody = getRequestBody(smevRequest.getBody(), smevRequest.getFiles(), smevRequest.getHasEmpowerment2021(), smevRequest.getAuthorityId(), smevRequest.getReusePaymentUin());
        HttpHeaders httpHeaders = getHttpHeaders(smevRequest);
        Map<String, ?> uriVariables = getUriVariables(smevRequest);

        String endpoint = uddiService.getEndpoint(UDDI_ENDPOINT);
        try {
            log.info("Have request to Service processing({}) : {}, uriVariables: {}", endpoint + PATH, requestBody, uriVariables);
            restTemplate.exchange(endpoint + PATH,
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, httpHeaders),
                    String.class,
                    uriVariables
            );
        } catch (HttpClientErrorException e) {
            val spRequestError = new SpRequestErrorDto();
            spRequestError.setCause(e.getMessage());
            spRequestError.setHeaders(httpHeaders.toSingleValueMap());
            spRequestError.setUrl(endpoint + PATH);
            spRequestError.setBody(requestBody);
            throw new SpRequestException("Ошибка отправки в SP", spRequestError, e);
        } catch (Exception e) {
            throw new SpAdapterServiceException(
                    "Error while send to service-processing " + smevRequest.getBody() + ", " + e.getMessage(), e);
        }
    }

    private String getRequestBody(String body, Set<String> files, Boolean hasEmpowerment2021, String authorityId, String reusePaymentUin) {
        var requestBody = new ServiceProcessingDto();
        AdditionalParametersDto additionalParams = null;
        try {
            if (body != null)
                requestBody.setBody(body);
            if (files != null)
                requestBody.setFiles(files);
            if (hasEmpowerment2021!=null && hasEmpowerment2021) {
                additionalParams = Objects.isNull(additionalParams) ? new AdditionalParametersDto() : additionalParams;
                additionalParams.setHasEmpowerment2021(Boolean.toString(hasEmpowerment2021));
            }
            if(Objects.nonNull(authorityId)){
                additionalParams = Objects.isNull(additionalParams) ? new AdditionalParametersDto() : additionalParams;
                additionalParams.setAuthorityId(authorityId);
                additionalParams.setHasEmpowerment(true);
            }
            if (reusePaymentUin != null) {
                additionalParams = Objects.isNull(additionalParams) ? new AdditionalParametersDto() : additionalParams;
                additionalParams.setReusePaymentUin(reusePaymentUin);
            }
            requestBody.setAdditionalParam(additionalParams);
            return objectMapper.writeValueAsString(requestBody);
        } catch (IOException e) {
            throw new SpAdapterServiceException("Error while creating request body from " + body, e);
        }
    }

    private HttpHeaders getHttpHeaders(SmevRequest smevRequest) {
        HttpHeaders httpHeaders = getFixedHttpHeaders(smevRequest.getRequestGuid(), smevRequest.getSystemAuthority(), smevRequest.getSkip17Status());
        if (smevRequest.getAdditionalHttpHeader() != null) {
            MultiValueMap<String, String> customHttpHeader = new LinkedMultiValueMap<String, String>();
            customHttpHeader.setAll(smevRequest.getAdditionalHttpHeader());
            httpHeaders.putAll(customHttpHeader);
        }
        log.info("SmevRequest httpHeader: {}", httpHeaders);
        return httpHeaders;
    }

    private HttpHeaders getFixedHttpHeaders(String requestGuid, String systemAuthority, Boolean skip17Status) {
        return new HttpHeaders() {{
            put(HttpHeaders.CONTENT_TYPE, List.of("application/xml;charset=UTF-8"));
            put("nowait", List.of("true"));
            if(skip17Status){
                put("skip17status", List.of("true"));
            }
            if (requestGuid != null)
                put("guid", List.of(requestGuid));
            if (systemAuthority != null)
                put("systemAuthority", List.of(systemAuthority));
        }};
    }

    private Map<String,?> getUriVariables(SmevRequest smevRequest) {
        String uriServiceCode = smevRequest.getServiceIdCustomName() != null
                ? smevRequest.getServiceIdCustomName()
                : smevRequest.getServiceCode();
        String strOrgId = isNull(smevRequest.getOrgId()) ? "" : smevRequest.getOrgId().toString();
        String strOrgType = getOrgTypeAbbr(smevRequest);
        String token = String.format("%d@%s@AL20@%s@1", smevRequest.getOid(), strOrgId, strOrgType);
        return Map.of(
                "uriServiceCode", uriServiceCode,
                "orderId", smevRequest.getOrderId(),
                "token", token
        );
    }

    private String getOrgTypeAbbr(SmevRequest smevRequest) {
        val orgType = smevRequest.getOrgType();
        if (orgType == null) return "";
        switch (orgType) {
            case LEGAL:
            case AGENCY: { return "L"; }
            case BUSINESS: { return "B"; }
            default: return "";
        }
    }
}
