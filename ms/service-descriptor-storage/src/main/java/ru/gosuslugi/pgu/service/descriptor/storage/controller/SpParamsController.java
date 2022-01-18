package ru.gosuslugi.pgu.service.descriptor.storage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SpParamsService;

@RestController
@RequiredArgsConstructor
public class SpParamsController {

    private final SpParamsService spParamsService;

    /**
     * GET /sp/params/{serviceId} : Получение parameters по идентификатору сервиса
     *
     * @param serviceId Идентификатор сервиса (required)
     * @return OK (status code 200) и подчасть конфига spConfig
     */
    @RequestMapping(value = "/sp/params/{serviceId}", method = RequestMethod.GET)
    ResponseEntity<String> get(@PathVariable("serviceId") String serviceId) {
        String params = spParamsService.get(serviceId);
        return ResponseEntity.ok(params);
    }

}
