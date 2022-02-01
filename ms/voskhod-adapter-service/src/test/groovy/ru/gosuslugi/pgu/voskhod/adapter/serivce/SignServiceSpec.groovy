package ru.gosuslugi.pgu.voskhod.adapter.serivce

import brave.Tracer
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.ArrayOfCertificateUserInfo
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CertificateUserInfo
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CreateUIToSingResponse
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.FileCertificateUserInfo
import ru.atc.idecs.integration.ws.esep.internal.InternalESEPIntegrationService
import ru.atc.idecs.integration.ws.esep.internal.SaveESEPSigningFileRequest
import ru.atc.idecs.integration.ws.esep.internal.SaveESEPSigningFileResponse
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException
import ru.gosuslugi.pgu.common.logging.rest.interceptor.ExternalServiceInterceptor
import ru.gosuslugi.pgu.common.logging.service.SpanService
import ru.gosuslugi.pgu.dto.SmevRequestDto
import ru.gosuslugi.pgu.dto.esep.PrepareSignRequest
import ru.gosuslugi.pgu.dto.esep.SignedFileInfo
import ru.gosuslugi.pgu.voskhod.adapter.mapper.EsepMapper
import ru.gosuslugi.pgu.voskhod.adapter.mapper.EsepMapperImpl
import ru.gosuslugi.pgu.voskhod.adapter.service.SignService
import ru.gosuslugi.pgu.sp.adapter.SpAdapterClient;
import ru.gosuslugi.pgu.voskhod.adapter.service.esep.EsepServiceHelper
import ru.nvg.idecs.storageservice.ws.common.data.DataService
import ru.nvg.idecs.storageservice.ws.common.data.OrderAttachmentsRequest
import ru.nvg.idecs.storageservice.ws.common.data.SendAttachmentsToESEPResponse
import ru.nvg.idecs.storageservice.ws.types.EsepFile
import spock.lang.Specification

import java.util.function.Supplier

class SignServiceSpec extends Specification {

    static def USER_ID = 1L
    static def ORDER_ID = 1L
    static def REQUEST_GUID="345234513451";
    static def FILE_ACCESS_CODE = '487b9c4c-dc8e-43ac-bfb9-21f6a4325158'
    static def FILE_MNEMONIC = 'req_preview.pdf'
    static def USER_COMMON_NAME = 'Иван Иванов'
    static def USER_SNILS = '000-000-000 00'
    static def RETURN_URL = 'http://returnUrl.test'
    static def SIGN_URL = 'http://signUrl.test'
    static def OPERATION_ID = '1'

    DataService dataService
    EsepServiceHelper esepServiceHelper
    InternalESEPIntegrationService internalESEPIntegrationService
    EsepMapper esepMapper
    SignService signService
    SpanService spanService

    void setup() {

        dataService = Mock(DataService) {
            it.sendAttachmentsToESEP(_ as OrderAttachmentsRequest) >> { sendAttachmentsToESEPResponse() }
        }

        esepServiceHelper = Mock(EsepServiceHelper) {
            it.createUIToSingEx(_ as String, _ as List<EsepFile>) >> { uIToSingResponse() }
            it.getFileCertificateUserInfos(_ as List<String>) >> { fileCertificateUserInfos() }
        }

        internalESEPIntegrationService = Mock(InternalESEPIntegrationService) {
            it.saveESEPSigningFile(_ as SaveESEPSigningFileRequest) >> { new SaveESEPSigningFileResponse() }
        }

        esepMapper = new EsepMapperImpl()

        spanService = Mock(SpanService) {
            it.runExternalService(_ as String, _ as String, _ as Supplier, _ as Map) >> { s1, s2, func, m -> func.get() }
        }

        signService = new SignService(
                Mock(SpAdapterClient) { it.createXmlAndPdf(ORDER_ID, USER_ID) >> { new SmevRequestDto(FILE_ACCESS_CODE) } },
                dataService,
                esepServiceHelper,
                internalESEPIntegrationService,
                esepMapper,
                spanService
        )
    }

    def "PrepareSign should work properly"() {
        when:
        def signRequest = new PrepareSignRequest();
        signRequest.setOrderId(ORDER_ID)
        signRequest.setReturnUrl(RETURN_URL)
        signRequest.setUserId(USER_ID)
        signRequest.setRequestGuid(REQUEST_GUID)
        signRequest.setFilesAlreadyExists(false)
        def prepareSignResponse = signService.prepareSign(signRequest)

        then:
        prepareSignResponse != null
        prepareSignResponse.operationID == OPERATION_ID
        prepareSignResponse.url == SIGN_URL
        def signInfoValue = new SignedFileInfo(FILE_ACCESS_CODE, FILE_MNEMONIC)
        prepareSignResponse.signedFileInfos == [signInfoValue]
    }

    def "getFileCertificatesUserInfo should work properly"() {
        when:
        def response = signService.getFileCertificatesUserInfo([FILE_ACCESS_CODE])

        then:
        response.certificateInfoDtoList.first().certificateUserInfoList.first().commonName == USER_COMMON_NAME
        response.certificateInfoDtoList.first().certificateUserInfoList.first().snils == USER_SNILS
        response.certificateInfoDtoList.first().fileAccessCode == FILE_ACCESS_CODE
    }

    def "Should send attachments to ESEP"() {
        when:
        def response = signService.sendAttachmentsToESEP(ORDER_ID)

        then:
        notThrown(ExternalServiceException)
        response.getFileAccessCodes().first().getFileAccessCode() == FILE_ACCESS_CODE
    }

    def "Should save ESEP signing file"() {
        given:
        CreateUIToSingResponse uiToSingResponse = uIToSingResponse()
        List<EsepFile> esepFileList = [esepFile()]

        when:
        signService.sendSignInfo(ORDER_ID, uiToSingResponse, esepFileList)

        then:
        1 * internalESEPIntegrationService.saveESEPSigningFile(_ as SaveESEPSigningFileRequest)
    }

    def "Should handle service errors"() {
        given:
        ru.nvg.idecs.storageservice.ws.types.Error error = new ru.nvg.idecs.storageservice.ws.types.Error()
        error.setErrorCode(1L)
        error.setErrorMessage('Error message')

        when:
        signService.handlePossibleServiceError('Error wrapper message', error)

        then:
        ExternalServiceException exception = thrown(ExternalServiceException)
        exception.getMessage() == "Error wrapper message (code: ${error.getErrorCode()}, message: ${error.getErrorMessage()})"
    }

    private static def sendAttachmentsToESEPResponse() {
        SendAttachmentsToESEPResponse response = new SendAttachmentsToESEPResponse()
        EsepFile file = esepFile()
        response.fileAccessCodes = [file]
        response.setError(new ru.nvg.idecs.storageservice.ws.types.Error())
        return response
    }

    private static def esepFile() {
        EsepFile file = new EsepFile()
        file.setFileAccessCode(FILE_ACCESS_CODE)
        file.setObjectId(1L)
        file.setObjectType(1L)
        file.setMnemonic(FILE_MNEMONIC)
        return file
    }

    private static def uIToSingResponse() {
        CreateUIToSingResponse response = new CreateUIToSingResponse()
        response.setOperationID(OPERATION_ID)
        response.setUrl(SIGN_URL)
        return response
    }

    private static def fileCertificateUserInfos() {
        FileCertificateUserInfo info = new FileCertificateUserInfo()
        info.setFileAccessCode(FILE_ACCESS_CODE)
        info.setCertificatesUserInfo(new ArrayOfCertificateUserInfo())
        CertificateUserInfo userInfo = new CertificateUserInfo()
        userInfo.setCommonName(USER_COMMON_NAME)
        userInfo.setSNILS(USER_SNILS)
        info.getCertificatesUserInfo().certificateUserInfo = [userInfo]
        return [info]
    }
}
