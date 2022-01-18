package ru.gosuslugi.pgu.generator

import ru.gosuslugi.pgu.generator.exception.XmlUnmarshallingException
import ru.gosuslugi.pgu.generator.model.appeal.scenario.GetAppealScenarioResponse
import ru.gosuslugi.pgu.generator.service.XmlUnmarshallService
import spock.lang.Specification

class XmlUnmarshallServiceTest extends Specification {

    def service = new XmlUnmarshallService()

    def 'Check xml unmarshalling'() {
        when:
        def result = service.unmarshal(getClass().getResource('/xml/Response1.xml').text, GetAppealScenarioResponse.class)

        then:
        assert result != null
        assert result.scenario.document.description == 'Печатная форма постановления'
    }

    def 'Throw exception on invalid xml'() {
        when:
        service.unmarshal('<a>test</a>', GetAppealScenarioResponse.class)

        then:
        thrown(XmlUnmarshallingException)
    }

}
