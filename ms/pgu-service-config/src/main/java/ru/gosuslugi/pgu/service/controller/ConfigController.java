package ru.gosuslugi.pgu.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.gosuslugi.pgu.service.holder.FrontendConfigHolder;

@RestController
@RequestMapping("/config/{serviceId}")
@RequiredArgsConstructor
public class ConfigController {

    @Value("${acceptFromPost}")
    private Boolean acceptFromPost;

    private final FrontendConfigHolder frontendConfigHolder;

    @GetMapping
    public Object getConfig(@PathVariable String serviceId){
        return frontendConfigHolder.getConfigByServiceId(serviceId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity setConfig(@PathVariable String serviceId, @RequestBody Object config) throws Exception {
        if(acceptFromPost){
            frontendConfigHolder.setConfigByServiceId(serviceId,config);
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

}
