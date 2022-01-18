package ru.gosuslugi.pgu.sp.adapter.service;


import ru.gosuslugi.pgu.dto.DevSpAdapterDto;
import ru.gosuslugi.pgu.dto.pdf.RenderTemplateResponse;

/**
 * This service contains methods for templates development & debug processes
 */
public interface DevModeService {
    RenderTemplateResponse getServiceBusinessXml(DevSpAdapterDto requestDto);
    RenderTemplateResponse getServiceTransportXml(DevSpAdapterDto requestDto);
    RenderTemplateResponse getPdfRenderedJson(DevSpAdapterDto requestDto);
    byte[] getServicePdf(DevSpAdapterDto requestDto);
    byte[] getServiceAdditionalPdf(DevSpAdapterDto requestDto, String type);

}
