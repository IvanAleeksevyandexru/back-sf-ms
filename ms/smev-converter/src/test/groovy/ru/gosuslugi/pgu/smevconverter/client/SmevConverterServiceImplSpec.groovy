package ru.gosuslugi.pgu.smevconverter.client

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import ru.gosuslugi.pgu.client.draftconverter.DraftConverterClient
import ru.gosuslugi.pgu.dto.SmevConverterGetRequestDto
import ru.gosuslugi.pgu.dto.XmlCustomConvertRequest
import ru.gosuslugi.pgu.smevconverter.model.BarbarbokResponseDto
import ru.gosuslugi.pgu.smevconverter.service.SmevConverterServiceImpl
import spock.lang.Specification

class SmevConverterServiceImplSpec extends Specification {

    def 'when get result ok'() {
        given:
        def requestDto = new SmevConverterGetRequestDto(
                '<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns1:InputData xmlns:ns1=\"http://ffoms.ru/GetMedicalAttachment/2.0.0\"><ns1:FamilyName>Каменев</ns1:FamilyName><ns1:FirstName>Игорь</ns1:FirstName><ns1:Patronymic>Витальевич</ns1:Patronymic><ns1:BirthDate>1975-10-25</ns1:BirthDate><ns1:UnitedPolicyNumber>7708241323081874</ns1:UnitedPolicyNumber></ns1:InputData>',
                '10000000360',
                'MedicalOrgList')
        def responseData = """<OutputData xmlns=\\"http://ffoms.ru/GetMedicalAttachment/2.0.0\\" xmlns:ns2=\\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\\" xmlns:ns3=\\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.1\\" xmlns:soap=\\"http://schemas.xmlsoap.org/soap/envelope/\\" xmlns:xml=\\"http://www.w3.org/XML/1998/namespace\\"><MedicalOrganization>ООО МЦ \\"ГИНЕЯ\\"</MedicalOrganization><MedicalOrganizationAddress>Г.СМОЛЕНСК, УЛ. ПРЖЕВАЛЬСКОГО, Д.6/25, ОФИС 66</MedicalOrganizationAddress><MedicalOrganizationOID>5.5.555.5.5.55.55.55.5.55.5555</MedicalOrganizationOID><MedicalOrganizationCode>480044</MedicalOrganizationCode><DateAttachment>1958-04-30</DateAttachment><Region>66000</Region><RegionName>Смоленская область</RegionName></OutputData>"""
        def smevClient = Stub(SmevClient) {
            it.get(requestDto.getData()) >>
                    new ResponseEntity<>(
                            new BarbarbokResponseDto(data: responseData),
                            HttpStatus.OK
            )
        }

        def convertRequest = new XmlCustomConvertRequest(responseData, requestDto.getServiceId(), requestDto.getTemplateName(), requestDto.getExtData())
        def draftConverter = Stub(DraftConverterClient) {
            it.convertXmlCustom(convertRequest) >> { [
                    "MedicalOrganization": "ООО МЦ \"ГИНЕЯ\"",
                    "MedicalOrganizationAddress": "Г.СМОЛЕНСК, УЛ. ПРЖЕВАЛЬСКОГО, Д.6/25, ОФИС 66",
                    "MedicalOrganizationOID": "5.5.555.5.5.55.55.55.5.55.5555",
                    "MedicalOrganizationCode": "480044",
                    "DateAttachment": "1947-07-12",
                    "Region": "66000",
                    "RegionName": "Смоленская область"
            ] as Map<Object, Object> }
        }
        def smevConverter = new SmevConverterServiceImpl(smevClient, draftConverter)

        when:
        def result = smevConverter.get(requestDto)
        def d = result.getBody()

        then:
        result.getStatusCode() == HttpStatus.OK
        result.getBody().getOrDefault('MedicalOrganizationCode', null) == '480044'
    }

    def 'when broken request'() {
        given:
        def requestDto = new SmevConverterGetRequestDto(
                'abrakadabra',
                '10000000360',
                'MedicalOrgList')
        def responseData = """{"timestamp": "2022-02-15T22:27:51.201+00:00",
                                        "status": 500,
                                        "error": "Internal Server Error",
                                        "path": "/barbarbok/internal/api/barbarbok/v1/get"}"""
        def smevClient = Stub(SmevClient) {
            it.get(requestDto.getData()) >>
                    new ResponseEntity<>(
                            new BarbarbokResponseDto(data: responseData),
                            HttpStatus.OK
            )
        }

        def convertRequest = new XmlCustomConvertRequest(responseData, requestDto.getServiceId(), requestDto.getTemplateName(), requestDto.getExtData())
        def draftConverter = Stub(DraftConverterClient) {
            it.convertXmlCustom(convertRequest) >> { [
                    "MedicalOrganization": "ООО МЦ \"ГИНЕЯ\"",
                    "MedicalOrganizationAddress": "Г.СМОЛЕНСК, УЛ. ПРЖЕВАЛЬСКОГО, Д.6/25, ОФИС 66",
                    "MedicalOrganizationOID": "5.5.555.5.5.55.55.55.5.55.5555",
                    "MedicalOrganizationCode": "480044",
                    "DateAttachment": "1947-07-12",
                    "Region": "66000",
                    "RegionName": "Смоленская область"
            ] as Map<Object, Object> }
        }
        def smevConverter = new SmevConverterServiceImpl(smevClient, draftConverter)

        when:
        def result = smevConverter.get(requestDto)
        def d = result.getBody()

        then:
        result.getStatusCode() == HttpStatus.OK
        result.getBody().getOrDefault('MedicalOrganizationCode', null) == '480044'
    }
}