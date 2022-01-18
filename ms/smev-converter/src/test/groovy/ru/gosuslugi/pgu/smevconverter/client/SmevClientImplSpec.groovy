package ru.gosuslugi.pgu.smevconverter.client

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import ru.gosuslugi.pgu.common.core.exception.ExternalServiceException
import ru.gosuslugi.pgu.smevconverter.config.SmevClientProperties
import ru.gosuslugi.pgu.smevconverter.model.SmevServiceResponseDto
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@Ignore
class SmevClientImplSpec extends Specification {

    @Shared
    RestTemplate restTemplate
    MockRestServiceServer mockServer
    SmevClientProperties properties
    SmevClient apiClient

    def setupSpec() {
        restTemplate = new RestTemplate()
    }

    def setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
        properties = new SmevClientProperties(url: 'http://url_to_barbarbok', smevVersion: 'v30', timeout: 300, ttl: 100)
        apiClient = new SmevClientImpl(restTemplate, properties)
    }

    def get() {
        given:
        def xmlRequest = 'xml-request'
        def expectedDto = new SmevServiceResponseDto(id: 'a31e9628-2bb8-4086-9387-c8d54e726ffd', ttl: 10, status: 'DONE', data: 'output xml data')
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(properties.url + '/barbarbok/internal/api/barbarbok/v1/get')))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json('{"data": "' + xmlRequest + '", "ttl": ' + properties.ttl + ', "smevVersion": "' + properties.smevVersion + '", "timeout": ' + properties.timeout + '}'))
                .andRespond(
                        withSuccess(
                                '{"id": "' + expectedDto.id + '", "ttl": ' + expectedDto.ttl + ', "status": "' + expectedDto.status + '", "data": "' + expectedDto.data + '"}',
                                MediaType.APPLICATION_JSON))
        when:
        def responseDto = apiClient.get(xmlRequest)

        then:
        expectedDto.equals(responseDto)
        mockServer.verify()
    }

    def exceptions() {
        given:
        mockServer.expect(requestTo(new URI(properties.url + '/barbarbok/internal/api/barbarbok/v1/get')))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(status))
        when:
        apiClient.get(_ as String)

        then:
        thrown(type)
        mockServer.verify()

        where:
        status                           | type
        HttpStatus.REQUEST_TIMEOUT       | ExternalServiceException.class
        HttpStatus.INTERNAL_SERVER_ERROR | ExternalServiceException.class
    }
}