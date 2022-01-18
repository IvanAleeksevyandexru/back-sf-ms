package ru.gosuslugi.pgu.generator.service

import ru.gosuslugi.pgu.generator.model.appeal.cycle.AppealCycleResponse
import ru.gosuslugi.pgu.generator.model.appeal.scenario.GetAppealScenarioResponse
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest
import ru.gosuslugi.pgu.generator.service.fines.AdditionalStepsAppealDescriptorGenerator
import ru.gosuslugi.pgu.generator.service.fines.AppealDescriptorGenerator
import spock.lang.Ignore
import spock.lang.Specification

@Ignore("Тест потерял актуальность, т.к. шаблон дескриптора услуги теперь хранится в хранилище, а не локально ")
class AppealDescriptorGeneratorTest extends Specification {

    def mainGenerator = new AppealDescriptorGenerator()
    def additionalGenerator = new AdditionalStepsAppealDescriptorGenerator(mainGenerator)

    def 'Generate Appeal service descriptor'() {
        given:
        def xmlResponse = new XmlUnmarshallService().unmarshal(getClass()
                .getResource('/GetAppealScenario/03/Response.xml').text, GetAppealScenarioResponse)
        def addXmlResponse = new XmlUnmarshallService().unmarshal(getClass()
                .getResource('/AppealCycle/04/Response.xml').text, AppealCycleResponse)

        when:
        def mainScenario = mainGenerator.generateMainScenario(null, xmlResponse)

        then:
        assert mainScenario.getScreens().size() == 30
        assert mainScenario.getApplicationFields().size() == 29
        assert mainScenario.getScreenRules().size() == 29

        when:
        def addScenario = additionalGenerator.addAdditionalStepsToService(mainScenario, addXmlResponse, new AppealFinesRequest(
                serviceId: 'test_001',
                routeNumber: '222',
                billNumber: "12345",
                billDate: "2019-07-03T00:07:20.000+00:00",
                token: "tttttoken"
        ))

        then:
        assert addScenario.getScreens().size() == 33
        assert addScenario.getApplicationFields().size() == 32
        assert addScenario.getScreenRules().size() == 31
    }
}
