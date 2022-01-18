/*
 * Copyright 2017 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */
package ru.gosuslugi.pgu.sp.adapter.service.draft;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.collections.MapUtils.isEmpty;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for {@link RestTemplate} methods
 *
 * @author vbalovnev
 */
public class RestResourceManualTest {

//    /** Dev URL */
//    public static final String DRAFT_URL = "http://dev01.pgu2-pub.test.gosuslugi.ru/drafts/internal/api/drafts/v3/{id}";
//    public static final long START_ORDER_ID = 763512104L;

    /** UAT URL */
    public static final String DRAFT_URL = "http://pgu-uat-fednlb.test.gosuslugi.ru/drafts2/internal/api/drafts/v3/{id}";
    public static final long START_ORDER_ID = 980184194L;


    public static final Map<String, String> HEADER_MAP = new HashMap<>();
    static {
        HEADER_MAP.put("token", "1@1@1@1@1@");
    }

    /** Default charset */
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private RestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
        final JacksonTestConfig config = new JacksonTestConfig();
        restTemplate = new RestTemplate(Collections.singletonList(config.jackson2Converter()));
    //    restTemplate.setMessageConverters(messageConverters);
        restTemplate.setErrorHandler(
            getErrorHandler(config.objectMapper())
        );
    }

    @Ignore
    @Test
    public void test() throws Exception {
        for (long i = START_ORDER_ID; true; i++) {
            String resultString = null;
            try {
                resultString = getString(i);
                assertNotNull(resultString);
            } catch (Exception e) {
                if (HttpStatus.NOT_FOUND.name().equals(e.getMessage())) {
                    continue;
                } else {
                    throw e;
                }
            }
            DraftHolderDto dto = null;
            try {
                dto = JsonProcessingUtil.fromJson(resultString, DraftHolderDto.class);
            } catch (Exception e) {
                continue;
            }
            if (
                "10000000101".equals(dto.getType())
                && Optional.of(dto).map(DraftHolderDto::getBody).map(ScenarioDto::getAdditionalParameters).map(ap -> ap.get("oid")).filter(oid -> !isBlank(oid)).isPresent()
            ) {
                save(i, resultString);
            }
        }
    }

    private void save(long orderId, String resultString) throws IOException {

        File dir = new File("src/test/resources/xml/xml-zagranpassport-" + orderId);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileUtils.writeStringToFile(new File(dir, "draft.json"), resultString, StandardCharsets.UTF_8.name());
    }

    private String getString(long orderId) {
        String resultString = restTemplate.execute(
            DRAFT_URL,
            HttpMethod.GET,
            restTemplate.httpEntityCallback(createHttpEntity(HEADER_MAP), String.class),
            clientHttpResponse -> IOUtils.toString(clientHttpResponse.getBody(), StandardCharsets.UTF_8),
            Collections.singletonMap("id", orderId)
        );
        return resultString;
    }

    private <T> HttpEntity<T> createHttpEntity(final Map<String, String> headers) {
        return createHttpEntity(null, headers);
    }

    private <T> HttpEntity<T> createHttpEntity(final T body, final Map<String, String> headers) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        if (!isEmpty(headers)) {
            headers.forEach(httpHeaders::set);
        }
        return new HttpEntity<>(body, httpHeaders);
    }

    /**
     * Return default error handler
     * @param objectMapper object mapper
     * @return error handler
     */
    public static ResponseErrorHandler getErrorHandler(final ObjectMapper objectMapper) {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode() != HttpStatus.OK;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

                throw new RuntimeException(response.getStatusCode().name());
            }
        };
    }
}