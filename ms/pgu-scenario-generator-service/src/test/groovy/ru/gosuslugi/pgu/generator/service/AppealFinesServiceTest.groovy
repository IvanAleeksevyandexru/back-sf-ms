package ru.gosuslugi.pgu.generator.service

import com.fasterxml.jackson.databind.ObjectMapper
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor
import ru.gosuslugi.pgu.generator.client.IpshRestClient
import ru.gosuslugi.pgu.generator.model.appeal.scenario.GetAppealScenarioResponse
import ru.gosuslugi.pgu.generator.model.dto.AppealFinesRequest
import ru.gosuslugi.pgu.generator.service.fines.AdditionalStepsAppealDescriptorGenerator
import ru.gosuslugi.pgu.generator.service.fines.AppealDescriptorGenerator
import ru.gosuslugi.pgu.generator.service.fines.AppealFinesService
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient
import spock.lang.Specification

class AppealFinesServiceTest extends Specification {

    def appealFinesService = new AppealFinesService(
            Mock(AppealDescriptorGenerator),
            Mock(AdditionalStepsAppealDescriptorGenerator),
            Mock(XmlUnmarshallService),
            Mock(ServiceDescriptorClient),
            Mock(FileStorageService),
            Mock(IpshRestClient))

    def 'Test Time Parameter' () {
        given:
        def descriptor = new ServiceDescriptor();

        def req = new AppealFinesRequest();
        req.billNumber = "123321"
        req.routeNumber = "1"
        req.billDate = "someDateShouldBe"

        def res = new GetAppealScenarioResponse();
        res.id = "id"
        res.reqId = "reqId"
        def timestamp = Calendar.getInstance();
        timestamp.set(2021,01,01,0,0,0)
        timestamp.setTimeZone(TimeZone.default)
        res.timestamp = timestamp


        when:
         appealFinesService.saveParametersToDescriptor(descriptor,req,res)

        then:
        assert descriptor.getParameter("timestamp").get().startsWith("2021-02-01T00:00:00")
    }
}