package ru.gosuslugi.pgu.pdf.template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public GeneratePdfResponseDto generatePdf(@RequestBody @Valid GeneratePdfRequestDto request) {

        if (request.isAdditional()) {
            return new GeneratePdfResponseDto(pdfGeneratorService.createAdditionalApplicationPdf(request.getOrderId(), request.getUserId(), request.getPrefix(), request.getUserRole(), request.getDraft()));
        }
        return new GeneratePdfResponseDto(pdfGeneratorService.createApplicationPdf(request.getOrderId(), request.getUserId(), request.getPrefix(), request.getUserRole(), request.getDraft()));
    }

    @PostMapping("/attachments")
    public void generateAdditionalPdf(@RequestBody @Valid HandlePdfAttachmentsRequestDto request) {

        pdfGeneratorService.handlePdfAttachments(request);

    }


}
