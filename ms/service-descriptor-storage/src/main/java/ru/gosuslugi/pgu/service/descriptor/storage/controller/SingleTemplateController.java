package ru.gosuslugi.pgu.service.descriptor.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.service.descriptor.storage.controller.model.PutScenarioResponse;
import ru.gosuslugi.pgu.service.descriptor.storage.exception.TemplateNotFoundException;
import ru.gosuslugi.pgu.service.descriptor.storage.exception.WrongArchiveException;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SingleTemplateService;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/singletemplates")
public class SingleTemplateController {

    private final SingleTemplateService service;

    /** GET /v1/singletemplates/{serviceId}?path={path}
     * Получение единичного шаблона из архива файлов с шаблонами сервиса
     * @param serviceId идентификатор сервиса
     * @param path путь внутри архива с шаблонами
     * @return файл шаблона
     * @throws IOException если были ошибки ввода вывода
     */
    @GetMapping("/{serviceId}")
    @Operation(summary = "Получение единичного шаблона из архива файлов с шаблонами сервиса", responses = {
            @ApiResponse(responseCode = "200", description = "файл шаблона",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public ResponseEntity<ByteBuffer> get(@Parameter(name = "serviceId", in = ParameterIn.PATH, description = "ID сервиса", schema = @Schema(type = "string"))
                                              @PathVariable("serviceId") String serviceId,
                                          @Parameter(name = "path", in = ParameterIn.QUERY, description = "путь внутри архива с шаблонами", schema = @Schema(type = "string"))
                                            @RequestParam("path") String path) {
        try{
            ByteBuffer buffer = service.get(serviceId, path);
            if(buffer != null){
                return ResponseEntity.ok(buffer);
            } else {
                log.error(String.format("Не найден файл шаблона сервиса = %s, по пути = %s", serviceId, path));
                throw new TemplateNotFoundException();
            }
        } catch (IOException e) {
            log.error(String.format("Ошибка чтения файла шаблона сервиса = %s, по пути = %s", serviceId, path),e);
            throw new WrongArchiveException(e);
        }
    }

    /** GET /v1/singletemplates/{serviceId}/crc?path={path}
     * Получение CRC единичного шаблона из архива файлов с шаблонами сервиса
     * @param serviceId идентификатор сервиса
     * @param path путь внутри архива с шаблонами
     * @return файл шаблона
     * @throws IOException если были ошибки ввода вывода
     */
    @GetMapping("/{serviceId}/crc")
    @Operation(summary = "Получение CRC единичного шаблона из архива файлов с шаблонами сервиса", responses = {
            @ApiResponse(responseCode = "200", description = "CRC шаблона",
                    content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public ResponseEntity<Long> getCRC(@Parameter(name = "serviceId", in = ParameterIn.PATH, description = "ID сервиса", schema = @Schema(type = "string"))
                                           @PathVariable("serviceId") String serviceId,
                                       @Parameter(name = "path", in = ParameterIn.QUERY, description = "путь внутри архива с шаблонами", schema = @Schema(type = "string"))
                                           @RequestParam("path") String path) {
        try{
            Long crc = service.getCRC(serviceId, path);
            if(crc != null){
                return ResponseEntity.ok(crc);
            } else {
                log.error(String.format("Не найден файл шаблона сервиса = %s, по пути = %s", serviceId, path));
                throw new TemplateNotFoundException();
            }
        } catch (IOException e) {
            log.error(String.format("Ошибка чтения файла шаблона сервиса = %s, по пути = %s", serviceId, path),e);
            throw new WrongArchiveException(e);
        }
    }

}
