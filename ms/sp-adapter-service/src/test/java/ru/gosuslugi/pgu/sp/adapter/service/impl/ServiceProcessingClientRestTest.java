package ru.gosuslugi.pgu.sp.adapter.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.esia.search.service.UddiService;
import ru.gosuslugi.pgu.sp.adapter.data.SmevRequest;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

public class ServiceProcessingClientRestTest {

    // values smewRequest
    private static final String SERVICE_ID = "10000000303";
    private static final Long ORDER_ID = 1L;
    private static final Long OID = 1L;
    private static final Long ORGID = 5L;
    private static final String BODY = "body";
    private static final String REQUEST_GUID = "requestGuid";
    private static final String CUSTOM_SERVICE_ID = "serviceIdCustomName";
    private static final String SYSTEM_AUTHORITY = "systemAuthoority";

    private static final String ENDPOINT = "https://test.test.com";
    private static final String TOKEN_OID = "1@@AL20@@1";
    private static final String TOKEN_ORGID = "1@5@AL20@@1";
    private static final String UDDI_ENDPOINT = "uddi:gosuslugi.ru:services:service-processing";
    private static final String PATH = "/v1/ORDER_CALL/{uriServiceCode}/{orderId}/{token}";
    private static final String EMPTY_JSON_OBJECT = "{}";

    private static RestTemplate restTemplate;
    private static UddiService uddiService;

    private static ServiceProcessingClientRest sut;

    @BeforeClass
    public static void before() {
        restTemplate = mock(RestTemplate.class);
        uddiService = mock(UddiService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        // init mocks
        Mockito.when(uddiService.getEndpoint(UDDI_ENDPOINT)).thenReturn(ENDPOINT);
        ResponseEntity<String> responseRestTemplate = mock(ResponseEntity.class);
        Mockito.when(restTemplate.exchange(any(String.class),
                                            eq(HttpMethod.POST),
                                            any(HttpEntity.class),
                                            eq(String.class),
                                            any(Map.class)))
                .thenReturn(responseRestTemplate);

        sut = new ServiceProcessingClientRest(restTemplate, uddiService, objectMapper);
    }

    @Test
    public void shouldSmevRequestMinimal() {
        // given:
        SmevRequest smevRequest = SmevRequest.builder()
                .serviceCode(SERVICE_ID)
                .orderId(ORDER_ID)
                .oid(OID)
                .skip17Status(true)
                .requestGuid(REQUEST_GUID)
                .systemAuthority(SYSTEM_AUTHORITY)
                .build();

        // when:
        sut.orderCall(smevRequest);

        // then:
        Mockito.verify(restTemplate).exchange( ENDPOINT + PATH,
                HttpMethod.POST,
                new HttpEntity<>(EMPTY_JSON_OBJECT, getFixedHttpHeaders()),
                String.class,
                getUriVariablesWithServiceId());
    }

    @Test
    public void shouldSmevRequestWithCustomServiceId() {
        // given:
        SmevRequest smevRequest = SmevRequest.builder()
                .serviceCode(SERVICE_ID)
                .orderId(ORDER_ID)
                .oid(OID)
                .systemAuthority(SYSTEM_AUTHORITY)
                .requestGuid(REQUEST_GUID)
                .skip17Status(true)
                .serviceIdCustomName(CUSTOM_SERVICE_ID)
                .build();
        Map<String,?> uriVariablesWithCustomServiceId = Map.of(
                    "uriServiceCode", CUSTOM_SERVICE_ID,
                    "orderId", ORDER_ID,
                    "token", TOKEN_OID);

        // when:
        sut.orderCall(smevRequest);

        // then:
        Mockito.verify(restTemplate).exchange(ENDPOINT + PATH,
                HttpMethod.POST,
                new HttpEntity<>(EMPTY_JSON_OBJECT, getFixedHttpHeaders()),
                String.class,
                uriVariablesWithCustomServiceId);
    }

    @Test
    public void shouldSmevRequestStandart() {
        // given:
        SmevRequest smevRequest = SmevRequest.builder().serviceCode(SERVICE_ID).orderId(ORDER_ID).oid(OID)
                .systemAuthority(SYSTEM_AUTHORITY)
                .body(BODY)
                .files(getListFiles())
                .requestGuid(REQUEST_GUID)
                .skip17Status(true)
                .build();
        String jsonBody = "{\"files\":[\"file_1\"],\"body\":\"body\"}";

        // when:
        sut.orderCall(smevRequest);

        // then:
        Mockito.verify(restTemplate).exchange(ENDPOINT + PATH,
                HttpMethod.POST,
                new HttpEntity<>(jsonBody, getFixedHttpHeaders()),
                String.class,
                getUriVariablesWithServiceId());
    }

    @Test
    public void shouldSmevRequestWithCustomHttpHeaders() {
        // given:
        SmevRequest smevRequest = SmevRequest.builder().serviceCode(SERVICE_ID).orderId(ORDER_ID).oid(OID)
                .systemAuthority(SYSTEM_AUTHORITY)
                .requestGuid(REQUEST_GUID)
                .body(BODY)
                .skip17Status(true)
                .files(getListFiles())
                .additionalHttpHeader(getCustomHttpHeaderFromSmevRequest())
                .build();
        HttpHeaders allHttpHeader = getHttpHeadersWithCustom();
        String jsonBody = "{\"files\":[\"file_1\"],\"body\":\"body\"}";

        // when:
        sut.orderCall(smevRequest);

        // then:
        Mockito.verify(restTemplate).exchange(ENDPOINT + PATH,
                HttpMethod.POST,
                new HttpEntity<>(jsonBody, allHttpHeader),
                String.class,
                getUriVariablesWithServiceId());
    }

    @Test
    public void shouldSmevRequestForOrg() {
        // given:
        SmevRequest smevRequest = SmevRequest.builder().serviceCode(SERVICE_ID).orderId(ORDER_ID).oid(OID)
                .systemAuthority(SYSTEM_AUTHORITY)
                .requestGuid(REQUEST_GUID)
                .orgId(ORGID)
                .skip17Status(true)
                .build();
        Map<String,?> uriVariablesForOrg =  Map.of(
                "uriServiceCode", SERVICE_ID,
                "orderId", ORDER_ID,
                "token", TOKEN_ORGID
        );

        // when:
        sut.orderCall(smevRequest);

        // then:
        Mockito.verify(restTemplate).exchange(ENDPOINT + PATH,
                HttpMethod.POST,
                new HttpEntity<>(EMPTY_JSON_OBJECT, getFixedHttpHeaders()),
                String.class,
                uriVariablesForOrg);
    }

    @Test
    public void shouldSmevRequestStandartWithEmpowerment() {
        // given:
        SmevRequest smevRequest = SmevRequest.builder().serviceCode(SERVICE_ID).orderId(ORDER_ID).oid(OID)
                .systemAuthority(SYSTEM_AUTHORITY)
                .body(BODY)
                .files(getListFiles())
                .requestGuid(REQUEST_GUID)
                .hasEmpowerment2021(true)
                .skip17Status(true)
                .build();
        String jsonBody = "{\"files\":[\"file_1\"],\"body\":\"body\",\"additionalParam\":{\"hasEmpowerment2021\":\"true\"}}";

        // when:
        sut.orderCall(smevRequest);

        // then:
        Mockito.verify(restTemplate).exchange(ENDPOINT + PATH,
                HttpMethod.POST,
                new HttpEntity<>(jsonBody, getFixedHttpHeaders()),
                String.class,
                getUriVariablesWithServiceId());
    }

    private HttpHeaders getFixedHttpHeaders() {
        return new HttpHeaders() {{
            put(HttpHeaders.CONTENT_TYPE, List.of("application/xml;charset=UTF-8"));
            put("nowait", List.of("true"));
            put("guid", List.of(REQUEST_GUID));
            put("skip17status", List.of("true"));
            put("systemAuthority", List.of(SYSTEM_AUTHORITY));
        }};
    }

    private Map<String,?> getUriVariablesWithServiceId() {
        return Map.of(
                "uriServiceCode", SERVICE_ID,
                "orderId", ORDER_ID,
                "token", TOKEN_OID
        );
    };

    private Set<String> getListFiles() {
        return Set.of("file_1");
    }

    private Map<String, String> getCustomHttpHeaderFromSmevRequest() {
        return Map.of(
                "customHeader", "valueCustomHeader",
                "systemAuthority", SYSTEM_AUTHORITY+"custom"
        );
    }

    private HttpHeaders getHttpHeadersWithCustom() {
        return new HttpHeaders() {{
            put(HttpHeaders.CONTENT_TYPE, List.of("application/xml;charset=UTF-8"));
            put("nowait", List.of("true"));
            put("guid", List.of(REQUEST_GUID));
            put("skip17status", List.of("true"));
            put("customHeader", List.of("valueCustomHeader"));
            put("systemAuthority", List.of(SYSTEM_AUTHORITY+"custom"));
        }};
    }

}
