package ru.gosuslugi.pgu.service.descriptor.storage.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<ByteBuffer> get(@PathVariable("serviceId") String serviceId, @RequestParam("path") String path) {
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
    public ResponseEntity<Long> getCRC(@PathVariable("serviceId") String serviceId, @RequestParam("path") String path) {
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
