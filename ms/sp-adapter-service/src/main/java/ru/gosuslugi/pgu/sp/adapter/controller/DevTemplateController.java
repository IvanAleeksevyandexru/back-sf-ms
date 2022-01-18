package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
    public @ResponseBody byte[] getBusinessXml(@RequestBody DevSpAdapterDto requestDto) {
        return devModeService.getServiceBusinessXml(requestDto).toString().getBytes();
    }

    @RequestMapping(value = "/transportXml", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody byte[] getTransport(@RequestBody DevSpAdapterDto requestDto) {
        return devModeService.getServiceTransportXml(requestDto).toString().getBytes();
    }

    @RequestMapping(value = "/pdfFormingJson", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public RenderTemplateResponse getPdfFormingJson(@RequestBody DevSpAdapterDto requestDto) {
        return devModeService.getPdfRenderedJson(requestDto);
    }

    @RequestMapping(value = "/servicePdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getPdf(@RequestBody DevSpAdapterDto requestDto) {
        return devModeService.getServicePdf(requestDto);

    }

    @RequestMapping(value = "/additionalPdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getAdditionalPdf(@RequestBody DevSpAdapterDto requestDto) {
        return devModeService.getServiceAdditionalPdf(requestDto, ADDITIONAL_APPLICATION_TEMPLATE_PREFIX);

    }

    @RequestMapping(value = "/aprPdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] getApprovalPdf(@RequestBody DevSpAdapterDto requestDto) {
        return devModeService.getServiceAdditionalPdf(requestDto, APPROVAL_APPLICATION_TEMPLATE_PREFIX);

    }

}
