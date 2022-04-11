package ru.gosuslugi.pgu.pdf.template.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.dto.pdf.GeneratePdfRequestDto;
import ru.gosuslugi.pgu.dto.pdf.GeneratePdfResponseDto;
import ru.gosuslugi.pgu.dto.pdf.HandlePdfAttachmentsRequestDto;
import ru.gosuslugi.pgu.pdf.template.service.PdfGeneratorService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/template", produces = MediaType.APPLICATION_JSON_VALUE)
public class PdfGeneratorController {

    private final PdfGeneratorService pdfGeneratorService;

    @PostMapping("/generate")
    @Operation(summary = "Генерация PDF", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = GeneratePdfResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public GeneratePdfResponseDto generatePdf(@RequestBody @Valid GeneratePdfRequestDto request) {

        if (request.isAdditional()) {
            return new GeneratePdfResponseDto(pdfGeneratorService.createAdditionalApplicationPdf(request.getOrderId(), request.getUserId(), request.getPrefix(), request.getUserRole(), request.getDraft()));
        }
        return new GeneratePdfResponseDto(pdfGeneratorService.createApplicationPdf(request.getOrderId(), request.getUserId(), request.getPrefix(), request.getUserRole(), request.getDraft()));
    }

    @PostMapping("/attachments")
    @Operation(summary = "Генерация дополнительных PDF", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public void generateAdditionalPdf(@RequestBody @Valid HandlePdfAttachmentsRequestDto request) {

        pdfGeneratorService.handlePdfAttachments(request);

    }


}
