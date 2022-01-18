package ru.gosuslugi.pgu.service.descriptor.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SpConfigService;

@RestController
@RequiredArgsConstructor
public class SpConfigController {

    private final SpConfigService service;

    /**
     * GET /sp/config/{serviceId} : Получение spConfig по идентификатору сервиса
     *
     * @param serviceId Идентификатор сервиса (required)
     * @return OK (status code 200) и подчасть конфига spConfig
     */
    @RequestMapping(value = "/sp/config/{serviceId}", method = RequestMethod.GET)
    ResponseEntity<String> get(@PathVariable("serviceId") String serviceId) {
        String params = service.get(serviceId);
        return ResponseEntity.ok(params);
    }

}
