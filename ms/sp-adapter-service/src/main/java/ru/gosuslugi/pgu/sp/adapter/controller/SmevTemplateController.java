package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.gosuslugi.pgu.dto.SmevRequestDto;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.sp.adapter.service.SmevService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SmevTemplateController {

    private final SmevService smevTemplateEngineService;

    /**
     * Method that sends one request to smev
     * Service processing adapter retrieves draft from draft service, processes service template and sends it to epgu service processing module
     */
    @RequestMapping(value = "/sendSmevRequest", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> sendDraftToSmev(@RequestBody SpAdapterDto dto) {
        return smevTemplateEngineService.processSmevRequest(dto.getServiceId(), dto.getOrderId(), dto.getOid(), dto.getRole(), dto.getOrgId(), false) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Method that sends batch of requests to smev
     * Service processing adapter retrieves draft from draft service, processes service template and sends it to epgu service processing module
     */
    @RequestMapping(value = "/sendBatchSmevRequest/{serviceId}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> sendDraftBatchToSmev(@PathVariable String serviceId, @RequestBody List<SpAdapterDto> dto) {
        return smevTemplateEngineService.processSmevRequest(serviceId, dto, false) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @RequestMapping(value = "/sendSignedSmevRequest", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> sendSignedSmevRequest(@RequestBody SpAdapterDto dto) {
        return smevTemplateEngineService.processSignedSmevRequest(dto.getServiceId(), dto.getOrderId(), dto.getOid(), dto.getRole(), dto.getOrgId(), false) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Method that create smew send request
     * @param dto order and user info
     * @return smev send request
     */
    @RequestMapping(value = "/createXmlAndPdf", method = RequestMethod.POST, produces = "application/json")
    public SmevRequestDto createXmlAndPdf(@RequestBody SpAdapterDto dto) {
        return smevTemplateEngineService.createXmlAndPdf(dto.getOrderId(), dto.getOid(), dto.getOrgId(), dto.getRequestGuid(), false);
    }
}
