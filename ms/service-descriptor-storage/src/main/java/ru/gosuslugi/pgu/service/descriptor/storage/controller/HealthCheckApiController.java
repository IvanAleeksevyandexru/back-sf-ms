package ru.gosuslugi.pgu.service.descriptor.storage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HealthCheckApiController {

    /**
     * GET /health-check/status : Проверка доступности сериса
     *
     * @return OK (status code 200)
     *         or Unauthorized (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     *         or Внутренняя ошибка (status code 500)
     */
    @RequestMapping(value = "/health-check/status",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("{\"status\":true}");
    }
}
