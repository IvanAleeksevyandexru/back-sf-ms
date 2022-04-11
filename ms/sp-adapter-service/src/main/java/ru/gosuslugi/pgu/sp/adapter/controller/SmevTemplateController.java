package ru.gosuslugi.pgu.sp.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Operation(summary = "Method that sends one request to smev\n" +
            "Service processing adapter retrieves draft from draft service, processes service template and sends it to epgu service processing module", responses = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Wrong parameters"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<?> sendDraftToSmev(@RequestBody(description = "DTO SpAdapterDto", content = @Content(schema=@Schema(implementation = SpAdapterDto.class))) SpAdapterDto dto) {
        return smevTemplateEngineService.processSmevRequest(dto.getServiceId(), dto.getOrderId(), dto.getOid(), dto.getRole(), dto.getOrgId(), false) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Method that sends batch of requests to smev
     * Service processing adapter retrieves draft from draft service, processes service template and sends it to epgu service processing module
     */
    @RequestMapping(value = "/sendBatchSmevRequest/{serviceId}", method = RequestMethod.POST, produces = "application/json")
    @Operation(summary = "Method that sends batch of requests to smev\n" +
            "Service processing adapter retrieves draft from draft service, processes service template and sends it to epgu service processing module", responses = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Wrong parameters"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<?> sendDraftBatchToSmev(
            @Parameter(name = "path", in = ParameterIn.PATH, description = "ID of service", schema = @Schema(type = "string"))
                @PathVariable String serviceId,
            @RequestBody(description = "List of DTO SpAdapterDto", content = @Content(array = @ArraySchema(schema=@Schema(implementation = SpAdapterDto.class))))
                List<SpAdapterDto> dto) {
        return smevTemplateEngineService.processSmevRequest(serviceId, dto, false) ?
                ResponseEntity.ok().build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @RequestMapping(value = "/sendSignedSmevRequest", method = RequestMethod.POST, produces = "application/json")
    @Operation(summary = "Method that sends signed requests to smev", responses = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Wrong parameters"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public ResponseEntity<?> sendSignedSmevRequest(@RequestBody(description = "DTO SpAdapterDto", content = @Content(schema=@Schema(implementation = SpAdapterDto.class))) SpAdapterDto dto) {
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
    @Operation(summary = "Method that create smev send request", responses = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Wrong parameters"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public SmevRequestDto createXmlAndPdf(@RequestBody(description = "DTO SpAdapterDto", content = @Content(schema=@Schema(implementation = SpAdapterDto.class))) SpAdapterDto dto) {
        return smevTemplateEngineService.createXmlAndPdf(dto.getOrderId(), dto.getOid(), dto.getOrgId(), dto.getRequestGuid(), false);
    }
}
