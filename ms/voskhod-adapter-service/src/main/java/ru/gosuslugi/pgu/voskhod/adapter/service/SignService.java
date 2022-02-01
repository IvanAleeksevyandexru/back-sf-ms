package ru.gosuslugi.pgu.voskhod.adapter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CreateUIToSingResponse;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.FileCertificateUserInfo;
import org.springframework.stereotype.Service;
import ru.atc.idecs.integration.ws.esep.internal.InternalESEPIntegrationService;
import ru.atc.idecs.integration.ws.esep.internal.SaveESEPSigningFileRequest;
import ru.atc.idecs.integration.ws.esep.internal.SaveESEPSigningFileResponse;
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.dto.esep.*;
import ru.gosuslugi.pgu.voskhod.adapter.mapper.EsepMapper;
import ru.gosuslugi.pgu.voskhod.adapter.service.esep.EsepServiceHelper;
import ru.nvg.idecs.common.util.ws.ResponseCode;
import ru.nvg.idecs.storageservice.ws.common.data.DataService;
import ru.nvg.idecs.storageservice.ws.common.data.OrderAttachmentsRequest;
import ru.nvg.idecs.storageservice.ws.common.data.SendAttachmentsToESEPResponse;
import ru.nvg.idecs.storageservice.ws.types.EsepFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Сервис для взаимодействия с сервисом Восход
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class SignService {

    private final SpAdapterClient spAdapterClient;
    private final DataService dataService;
    private final EsepServiceHelper esepServiceHelper;
    private final InternalESEPIntegrationService internalESEPIntegrationService;
    private final EsepMapper esepMapper;
    private final SpanService spanService;

    private static final String AUTH_TOKEN = "1";

    /**
     * Подготавливаем данные для подписания заявление
     * @param signRequest   Данные заявления для подписания
     * @return              Данные для подписания заявления
     */
    public PrepareSignResponse prepareSign(PrepareSignRequest signRequest) {
        requireNonNull(signRequest.getOrderId(), "orderId is empty");
        requireNonNull(signRequest.getUserId(), "userId is empty");
        requireNonNull(signRequest.getReturnUrl(), "returnUrl is empty");
        requireNonNull(signRequest.getFilesAlreadyExists(), "filesAlreadyExists is empty");
        if (!signRequest.getFilesAlreadyExists()) {
            requireNonNull(signRequest.getRequestGuid(), "requestGuid is empty in case files already signed");
        }


        if (!signRequest.getFilesAlreadyExists()) {
            spAdapterClient.createXmlAndPdf(signRequest.getOrderId(), signRequest.getUserId(), signRequest.getOrgId(), signRequest.getRequestGuid());
        }
        SendAttachmentsToESEPResponse attachmentsResponse = sendAttachmentsToESEP(signRequest.getOrderId());
        List<EsepFile> fileAccessCodes = attachmentsResponse.getFileAccessCodes();
        if (fileAccessCodes.isEmpty()) {
            throw new ExternalServiceException("Error while transferring attachments to ESEP: No file found for order (orderId = " + signRequest.getOrderId() + ")");
        }

        //TODO Для поддержки выборочной подписи требуется доработка сервиса подписания "Восход"
//        List<String> esepFileNames = List.of(PDF_FILE_NAME, XML_FILE_NAME.replace("{guid}", signRequest.getRequestGuid()));
//        fileAccessCodes = fileAccessCodes.stream().filter(it -> esepFileNames.contains(it.getMnemonic())).collect(Collectors.toList());

        CreateUIToSingResponse signResponse = esepServiceHelper.createUIToSingEx(signRequest.getReturnUrl(), fileAccessCodes);
        sendSignInfo(signRequest.getOrderId(), signResponse, fileAccessCodes);

        PrepareSignResponse signInfo = esepMapper.toPrepareSignResponse(signResponse);
        signInfo.setSignedFileInfos(fileAccessCodes.stream().map(it -> new SignedFileInfo(it.getFileAccessCode(), it.getMnemonic())).collect(Collectors.toList()));

        return signInfo;
    }

    /**
     * Получаем данные о подписании файлов
     * @param fileAccessCodes   Коды доступа файлов
     * @return                  Данные о подписании
     */
    public FileCertificatesUserInfoResponse getFileCertificatesUserInfo(List<String> fileAccessCodes) {
        List<FileCertificateUserInfo> response = esepServiceHelper.getFileCertificateUserInfos(fileAccessCodes);
        FileCertificatesUserInfoResponse result = new FileCertificatesUserInfoResponse();

        response.forEach(it -> {
            if (it.getCertificatesUserInfo() != null && it.getCertificatesUserInfo().getCertificateUserInfo() != null) {
                CertificateInfoDto certificateInfoDto = new CertificateInfoDto();
                certificateInfoDto.setFileAccessCode(it.getFileAccessCode());
                List<CertificateUserInfoDto> userInfoDtoList = it.getCertificatesUserInfo().getCertificateUserInfo()
                    .stream()
                    .map(userInfo -> CertificateUserInfoDto.builder()
                            .commonName(userInfo.getCommonName())
                            .snils(userInfo.getSNILS())
                            .inn(userInfo.getINN())
                            .ogrn(userInfo.getOGRN())
                            .ogrnip(userInfo.getOGRNIP())
                            .build())
                    .collect(Collectors.toList());
                certificateInfoDto.setCertificateUserInfoList(userInfoDtoList);
                result.getCertificateInfoDtoList().add(certificateInfoDto);
            }
        });

        return result;
    }

    private SendAttachmentsToESEPResponse sendAttachmentsToESEP(Long attachmentOrderId) {
        OrderAttachmentsRequest attachmentsRequest = new OrderAttachmentsRequest();
        attachmentsRequest.setOrderId(attachmentOrderId);
        attachmentsRequest.setAuthToken(AUTH_TOKEN);
        log.info("ESEP external request = {}", JsonProcessingUtil.toJson(attachmentsRequest));
        final SendAttachmentsToESEPResponse response = spanService.runExternalService(
                "dataService.sendAttachmentsToESEP",
                "dataService.sendAttachmentsToESEP",
                () -> dataService.sendAttachmentsToESEP(attachmentsRequest),
                Map.of("attachmentOrderId", String.valueOf(attachmentOrderId)));
        log.info("ESEP external response = {}",JsonProcessingUtil.toJson(response));
        handlePossibleServiceError("Error while transferring attachments to ESEP (orderId = " + attachmentOrderId +")", response.getError());

        return response;
    }

    private void sendSignInfo(Long orderId, CreateUIToSingResponse signResponse, List<EsepFile> fileAccessCodes) {
        final SaveESEPSigningFileRequest request = new SaveESEPSigningFileRequest();
        for (EsepFile esepFile : fileAccessCodes) {
            ru.atc.idecs.integration.ws.esep.internal.File file = new ru.atc.idecs.integration.ws.esep.internal.File();
            file.setObjectId(orderId);
            file.setObjectType(esepFile.getObjectType());
            file.setMnemonic(esepFile.getMnemonic());
            file.setEsepAccessCode(esepFile.getFileAccessCode());
            request.getFiles().add(file);
        }

        request.setOperationId(signResponse.getOperationID());
        log.info("ESEP external request = {}", JsonProcessingUtil.toJson(request));
        final SaveESEPSigningFileResponse response = spanService.runExternalService(
                "internalESEPIntegrationService.saveESEPSigningFile",
                "internalESEPIntegrationService.saveESEPSigningFile",
                () -> internalESEPIntegrationService.saveESEPSigningFile(request),
                Map.of("orderId", String.valueOf(orderId)));
        log.info("ESEP external response = {}", JsonProcessingUtil.toJson(response));
        if (response != null && response.getError() != null && response.getError().getCode() != ResponseCode.OK_RESULT.getCode()) {
            throw new ExternalServiceException("Can't save ESEP file info in LK (code: " + response.getError().getCode() + ", message: " +
                response.getError().getMessage() + ")");
        }
    }

    private void handlePossibleServiceError(String message, ru.nvg.idecs.storageservice.ws.types.Error error) {
        if (error != null && error.getErrorCode() != ResponseCode.OK_RESULT.getCode()) {
            throw new ExternalServiceException(message + " (code: " + error.getErrorCode() + ", message: " + error.getErrorMessage() +")");
        }
    }
}
