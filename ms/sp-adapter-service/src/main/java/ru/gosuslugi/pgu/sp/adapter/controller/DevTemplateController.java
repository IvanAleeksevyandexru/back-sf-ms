package ru.gosuslugi.pgu.sp.adapter.controller;

import ru.gosuslugi.pgu.dto.DevSpAdapterDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;
import ru.gosuslugi.pgu.sp.adapter.service.DevModeService;
import ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@ConditionalOnExpression("${dev-mode.enabled}")
@RequestMapping(value = "dev", produces = "application/json;charset=UTF-8")
public class DevTemplateController {

    private final DevModeService devModeService;

    @PostMapping(value = "/businessXml", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Тестовое получение business xml в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл business xml в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody
    byte[] getBusinessXml(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO DevSpAdapterDto",
                    content = @Content(
                            schema = @Schema(implementation = DevSpAdapterDto.class))
            ) DevSpAdapterDto requestDto) {
        return devModeService.getServiceBusinessXml(requestDto).toString().getBytes();
    }

    @PostMapping(value = "/transportXml", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Тестовое получение транспортной xml в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл транспортной xml в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody byte[] getTransport(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO DevSpAdapterDto",
                    content = @Content(
                            schema = @Schema(implementation = DevSpAdapterDto.class))
            ) DevSpAdapterDto requestDto) {
        return devModeService.getServiceTransportXml(requestDto).toString().getBytes();
    }

    @PostMapping(value = "/pdfFormingJson", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Тестовый рендеринг PDF", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RenderTemplateResponse.class)), description = "файл JSON с результатом рендеринга"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public RenderTemplateResponse getPdfFormingJson(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO DevSpAdapterDto",
                    content = @Content(schema = @Schema(implementation = DevSpAdapterDto.class))
            ) DevSpAdapterDto requestDto) {
        return devModeService.getPdfRenderedJson(requestDto);
    }

    @PostMapping(value = "/servicePdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Тестовое получение основной (common)"
            + " PDF в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл PDF в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody byte[] getPdf(@RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO DevSpAdapterDto",
                    content = @Content(schema = @Schema(implementation = DevSpAdapterDto.class))
            ) DevSpAdapterDto requestDto) {
        return devModeService.getServicePdf(requestDto);

    }

    @PostMapping(value = "/additionalPdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Тестовое получение дополнительной (additional)"
            + " PDF в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл PDF в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")})
    public @ResponseBody byte[] getAdditionalPdf(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "DTO DevSpAdapterDto",
                    content = @Content(schema = @Schema(implementation = DevSpAdapterDto.class))
            ) DevSpAdapterDto requestDto) {
        return devModeService.getServiceAdditionalPdf(requestDto,
                DefaultOptionsSpConfig.ADDITIONAL_APPLICATION_TEMPLATE_PREFIX);

    }

    @PostMapping(value = "/aprPdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Тестовое получение approval-PDF в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл PDF в виде потока " + "байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")})
    public @ResponseBody byte[] getApprovalPdf(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = DevSpAdapterDto.class)))
            DevSpAdapterDto requestDto) {
        return devModeService.getServiceAdditionalPdf(requestDto,
                DefaultOptionsSpConfig.APPROVAL_APPLICATION_TEMPLATE_PREFIX);
    }

}
