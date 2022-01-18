package ru.gosuslugi.pgu.xmlservice.controller;

import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;
import ru.gosuslugi.pgu.xmlservice.data.StoreResponse;
import ru.gosuslugi.pgu.xmlservice.service.XmlService;

import javax.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "services/xml")
@RequiredArgsConstructor
public class XmlController {

    private final XmlService xmlService;

    @PostMapping(value = "/generate/stream", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Генерирует содержимое XML файла в виде байт-потока")
    public byte[] generateStream(@RequestBody @Valid GenerateXmlRequest request) {
        return xmlService.generateStream(request);
    }

    @PostMapping(value = "/generate", produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Генерирует содержимое XML файла")
    public String generate(@RequestBody @Valid GenerateXmlRequest request) {
        return xmlService.generate(request);
    }

    @PostMapping(value = "/generateAndStore", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Генерирует содержимое XML файла и сохраняет его в хранилище")
    public StoreResponse generateAndStore(@RequestBody @Valid GenerateXmlRequest request) {
        return xmlService.generateAndStore(request);
    }
}
