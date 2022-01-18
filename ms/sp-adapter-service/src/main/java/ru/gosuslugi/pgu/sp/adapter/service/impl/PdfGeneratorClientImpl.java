package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.GeneratePdfRequestDto;
import ru.gosuslugi.pgu.dto.pdf.GeneratePdfResponseDto;
import ru.gosuslugi.pgu.dto.pdf.HandlePdfAttachmentsRequestDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sp.adapter.config.props.PdfGeneratorProperties;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterExternalServiceException;
import ru.gosuslugi.pgu.sp.adapter.service.SmevPdfService;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Slf4j
@RequiredArgsConstructor
public class PdfGeneratorClientImpl implements SmevPdfService {

    private static final String GENERATE_PDF_ENDPOINT = "/v1/template/generate";
    private static final String HANDLE_PDF_ATTACHMENT_ENDPOINT = "/v1/template/attachments";

    private final RestTemplate restTemplate;
    private final PdfGeneratorProperties properties;

    @Override
    public byte[] createApplicationPdf(Long orderId, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        GeneratePdfRequestDto request = new GeneratePdfRequestDto(pdfPrefix, orderId, oid, roleId, false, null);
        return callExternalGeneratePdfService(request);
    }

    @Override
    public byte[] createAdditionalApplicationPdf(Long orderId, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        GeneratePdfRequestDto request = new GeneratePdfRequestDto(pdfPrefix, orderId, oid, roleId, true, null);
        return callExternalGeneratePdfService(request);
    }

    private byte[] callExternalGeneratePdfService(GeneratePdfRequestDto request) {
        try {
            log.debug("Request PDF Generation by service {} with request {}", properties.getUrl() + GENERATE_PDF_ENDPOINT, request);
            ResponseEntity<GeneratePdfResponseDto> responseEntity = restTemplate.exchange(properties.getUrl() + GENERATE_PDF_ENDPOINT,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    GeneratePdfResponseDto.class
            );
            log.info("PDF Generation response: {}", responseEntity);
            return responseEntity.getBody() == null ? null : responseEntity.getBody().getPdfContent();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == NOT_ACCEPTABLE || e.getStatusCode().is5xxServerError()) {
                throw new SpAdapterExternalServiceException("Error while send to PDF Generator service: " + e.getMessage(), e);
            }
            log.error("Suppress error from PDF Generation Service: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Suppress error from PDF Generation Service: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void handlePdfAttachments(TemplatesDataContext templatesDataContext, FileDescription options) {
        HandlePdfAttachmentsRequestDto request = HandlePdfAttachmentsRequestDto.builder()
                .serviceId(templatesDataContext.getServiceId())
                .orderId(templatesDataContext.getOrderId())
                .oid(templatesDataContext.getOid())
                .orgId(templatesDataContext.getOrgId())
                .roleId(templatesDataContext.getRoleId() != null ? templatesDataContext.getRoleId().name() : null)
                .options(options)
                .build();

        try {
            log.debug("Request Handle PDF attachments by service {} with request {}", properties.getUrl() + HANDLE_PDF_ATTACHMENT_ENDPOINT, request);
            restTemplate.exchange(properties.getUrl() + HANDLE_PDF_ATTACHMENT_ENDPOINT,
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    Void.class
            );
            log.info("Handle PDF attachments successful");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == NOT_ACCEPTABLE || e.getStatusCode().is5xxServerError()) {
                throw new SpAdapterExternalServiceException("Error while send to PDF Generator service: " + e.getMessage(), e);
            }
            log.error("Suppress error from PDF Generation Service: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Suppress error from PDF Generation Service: {}", e.getMessage(), e);
        }
    }

    @Override
    public byte[] createApplicationPdf(ScenarioDto order, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        GeneratePdfRequestDto request = new GeneratePdfRequestDto(pdfPrefix, null, oid, roleId, false, order);
        return callExternalGeneratePdfService(request);
    }

    @Override
    public byte[] createAdditionalApplicationPdf(ScenarioDto order, Long oid, Long orgId, String pdfPrefix, String roleId, Boolean skip17Status) {
        GeneratePdfRequestDto request = new GeneratePdfRequestDto(pdfPrefix, null, oid, roleId, true, order);
        return callExternalGeneratePdfService(request);
    }

}
