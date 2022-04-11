package ru.gosuslugi.pgu.sp.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.DevSpAdapterDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;
import ru.gosuslugi.pgu.sp.adapter.service.DevModeService;

import static ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig.ADDITIONAL_APPLICATION_TEMPLATE_PREFIX;
import static ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig.APPROVAL_APPLICATION_TEMPLATE_PREFIX;


@RestController
@RequiredArgsConstructor
@ConditionalOnExpression("${dev-mode.enabled}")
@RequestMapping(value = "dev", produces = "application/json;charset=UTF-8")
public class DevTemplateController {

    private final DevModeService devModeService;

    @RequestMapping(value = "/businessXml", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Тестовое получение business xml в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл business xml в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody
    byte[] getBusinessXml(@RequestBody(description = "DTO DevSpAdapterDto", content = @Content(schema=@Schema(implementation = DevSpAdapterDto.class))) DevSpAdapterDto requestDto) {
        return devModeService.getServiceBusinessXml(requestDto).toString().getBytes();
    }

    @RequestMapping(value = "/transportXml", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Тестовое получение транспортной xml в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл транспортной xml в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody byte[] getTransport(@RequestBody(description = "DTO DevSpAdapterDto", content = @Content(schema=@Schema(implementation = DevSpAdapterDto.class))) DevSpAdapterDto requestDto) {
        return devModeService.getServiceTransportXml(requestDto).toString().getBytes();
    }

    @RequestMapping(value = "/pdfFormingJson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Тестовый рендеринг PDF", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = RenderTemplateResponse.class)), description = "файл JSON с результатом рендеринга"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public RenderTemplateResponse getPdfFormingJson(@RequestBody(description = "DTO DevSpAdapterDto", content = @Content(schema=@Schema(implementation = DevSpAdapterDto.class))) DevSpAdapterDto requestDto) {
        return devModeService.getPdfRenderedJson(requestDto);
    }

    @RequestMapping(value = "/servicePdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Тестовое получение PDF в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл PDF в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody byte[] getPdf(@RequestBody(description = "DTO DevSpAdapterDto", content = @Content(schema=@Schema(implementation = DevSpAdapterDto.class))) DevSpAdapterDto requestDto) {
        return devModeService.getServicePdf(requestDto);

    }

    @RequestMapping(value = "/additionalPdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Тестовое получение Additional PDF в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл PDF в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody byte[] getAdditionalPdf(@RequestBody(description = "DTO DevSpAdapterDto", content = @Content(schema=@Schema(implementation = DevSpAdapterDto.class))) DevSpAdapterDto requestDto) {
        return devModeService.getServiceAdditionalPdf(requestDto, ADDITIONAL_APPLICATION_TEMPLATE_PREFIX);

    }

    @RequestMapping(value = "/aprPdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Тестовое получение Approval PDF в виде потока байтов", responses = {
            @ApiResponse(responseCode = "200", description = "файл PDF в виде потока байтов"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка")
    })
    public @ResponseBody byte[] getApprovalPdf(@RequestBody DevSpAdapterDto requestDto) {
        return devModeService.getServiceAdditionalPdf(requestDto, APPROVAL_APPLICATION_TEMPLATE_PREFIX);

    }

}
