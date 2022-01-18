package ru.gosuslugi.pgu.sp.adapter.pgu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.esia.search.service.UddiService;
import ru.gosuslugi.pgu.core.service.client.UDDIKeys;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PguClientService {

    private final static String LK_API_DELETE_ORDER_PATH = "/v1/orders/{orderId}";

    private final UddiService uddiService;

    private final RestTemplate restTemplate;

    public void deleteOrderId(Long orderId) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            String endpoint = uddiService.getEndpoint(UDDIKeys.INTERNAL_LK_API);
            restTemplate.exchange(endpoint + LK_API_DELETE_ORDER_PATH,
                    HttpMethod.DELETE,
                    new HttpEntity<>(httpHeaders),
                    String.class,
                    Map.of("orderId", orderId)
            );
        } catch (RestClientException e) {
            log.error("Error while deleting order id {}", orderId, e);
        }
    }
}
