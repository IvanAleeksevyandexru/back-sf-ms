package ru.gosuslugi.pgu.smevconverter.client

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil
import ru.gosuslugi.pgu.smevconverter.config.SmevClientProperties
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus

class SmevClientImplSpec extends Specification {

    @Shared
    RestTemplate restTemplate
    @Shared
    SmevClientProperties properties
    MockRestServiceServer mockServer
    SmevClient apiClient
    static String apiPath = '/barbarbok/v1/'

    def setupSpec() {
        restTemplate = new RestTemplate()
        properties = new SmevClientProperties(
                url: 'http://pgu-uat-fednlb.test.gosuslugi.ru/barbarbok/internal/api',
                smevVersion: 'SMEV30MESSAGE',
                timeout: 60,
                ttl: 30)
    }

    def setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
        apiClient = new SmevClientImpl(restTemplate, properties, apiPath)
    }

    def 'pull. status 200 with errors'() {
        given:
        def id = '82c2018f-0f65-491e-ae9f-9ea61ef0d60e'
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(properties.url + apiPath + 'pull?id=' + id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(JsonProcessingUtil.toJson("""{
                                        "id": "82c2018f-0f65-491e-ae9f-9ea61ef0d60e",
                                        "ttl": 100,
                                        "status": "DONE",
                                        "smev3CallError": {
                                            "errorCode": "REJECT",
                                            "message": "NO_DATA:Сведения о прикреплении к медицинской организации не найдены"
                                        },
                                        "type": "common",
                                        "created": 1644907666811}""")))

        when:
        def response = apiClient.pull(id)

        then:
        mockServer.verify()
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getSmev3CallError() != null
        response.getBody().getSmev3CallError().getErrorCode() == 'REJECT'
        response.getBody().getData() == null
    }

    def 'pull. status 200 with data'() {
        given:
        def id = '82c2018f-0f65-491e-ae9f-9ea61ef0d60e'
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(properties.url + apiPath + 'pull?id=' + id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(JsonProcessingUtil.toJson("""{
                                        "id": "2084489e-c21f-48c7-bae7-2e8302c0d90c",
                                        "ttl": 100,
                                        "status": "DONE",
                                        "data": "<OutputData xmlns=\\"http://ffoms.ru/GetMedicalAttachment/2.0.0\\" xmlns:ns2=\\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/1.1\\" xmlns:ns3=\\"urn://x-artefacts-smev-gov-ru/services/message-exchange/types/faults/1.1\\" xmlns:soap=\\"http://schemas.xmlsoap.org/soap/envelope/\\" xmlns:xml=\\"http://www.w3.org/XML/1998/namespace\\">\\n<MedicalOrganization>ООО МЦ \\"ГИНЕЯ\\"</MedicalOrganization>\\n<MedicalOrganizationAddress>Г.СМОЛЕНСК, УЛ. ПРЖЕВАЛЬСКОГО, Д.6/25, ОФИС 66</MedicalOrganizationAddress>\\n<MedicalOrganizationOID>5.5.555.5.5.55.55.55.5.55.5555</MedicalOrganizationOID>\\n<MedicalOrganizationCode>480044</MedicalOrganizationCode>\\n<DateAttachment>1958-04-30</DateAttachment>\\n<Region>66000</Region>\\n<RegionName>Смоленская область</RegionName>\\n</OutputData>\\n",
                                        "type": "common",
                                        "created": 1644961334885
                                    }""")))

        when:
        def response = apiClient.pull(id)

        then:
        mockServer.verify()
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getSmev3CallError() == null
        response.getBody().getData() != null
    }

    def 'push. status 200'() {
        given:
        def id = '82c2018f-0f65-491e-ae9f-9ea61ef0d60e'
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(properties.url + apiPath + 'push')))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(JsonProcessingUtil.toJson('{"id": "'+ id +'"}')))

        when:
        def response = apiClient.push(_ as String)

        then:
        mockServer.verify()
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getId() == id
    }

    def 'get exceptions'() {
        given:
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(properties.url + apiPath + 'get')))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(httpStatus)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(JsonProcessingUtil.toJson([
                                        "timestamp" : "2022-01-01T15:30:33.685+00:00",
                                        "httpStatus": httpStatus.value(),
                                        "error"     : httpStatus.getReasonPhrase(),
                                        "path"      : "/barbarbok/internal/api/barbarbok/v1/get"
                                ] as Map)))

        when:
        def result = apiClient.get(_ as String)

        then:
        mockServer.verify()
        noExceptionThrown()
        result.getStatusCodeValue() == httpStatus.value()
        result.statusCode == httpStatus
        result.getBody() == null

        where:
        httpStatus << [HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR]
    }

    def 'push exceptions'() {
        given:
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(properties.url + apiPath + 'push')))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withStatus(httpStatus)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(JsonProcessingUtil.toJson([
                                        "timestamp" : "2022-02-15T22:27:34.179+00:00",
                                        "httpStatus": httpStatus.value(),
                                        "error"     : httpStatus.getReasonPhrase(),
                                        "path"      : "/barbarbok/internal/api/barbarbok/v1/push"
                                ] as Map)))

        when:
        def result = apiClient.push(_ as String)

        then:
        mockServer.verify()
        noExceptionThrown()
        result.getStatusCodeValue() == httpStatus.value()
        result.statusCode == httpStatus
        result.getBody() == null

        where:
        httpStatus << [HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR]
    }

    def 'pull exceptions'() {
        given:
        def id = '82c2018f-0f65-491e-ae9f-9ea61ef0d60e'
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(properties.url + apiPath + 'pull?id=' + id)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(httpStatus))
        when:
        def result = apiClient.pull(id)

        then:
        mockServer.verify()
        noExceptionThrown()
        result.getStatusCodeValue() == httpStatus.value()
        result.statusCode == httpStatus
        result.getBody() == null

        where:
        httpStatus << [HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR]
    }
}