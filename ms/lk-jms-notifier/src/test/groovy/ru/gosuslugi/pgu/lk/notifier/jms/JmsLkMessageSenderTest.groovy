package ru.gosuslugi.pgu.lk.notifier.jms

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.activemq.artemis.api.core.client.ClientSession
import org.apache.activemq.artemis.jms.client.compatible1X.ActiveMQTextCompatibleMessage
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator
import ru.gosuslugi.pgu.dto.lk.LkDataMessage
import ru.gosuslugi.pgu.dto.lk.SendNotificationRequestDto
import ru.gosuslugi.pgu.lk.notifier.model.dto.ScFormDataMessage
import spock.lang.Specification

import javax.jms.Destination
import javax.jms.Message
import javax.jms.Session

class JmsLkMessageSenderTest extends Specification {

    JmsTemplate jmsTemplate = Mock(JmsTemplate)
    ObjectMapper objectMapper = new ObjectMapper()
    Destination lkNotifyDestination = Stub(Destination)
    JmsLkMessageSender jmsLkMessageSender = new JmsLkMessageSender(jmsTemplate, objectMapper, lkNotifyDestination)
    Session session = Stub(Session)

    def 'TestSendMessages'(){

        given:
            SendNotificationRequestDto testSendNotificationRequestDto = new SendNotificationRequestDto(123L,
                List.of(new LkDataMessage('f1','f2','f3'), new LkDataMessage('k1','k2','k3')))
            session.createTextMessage() >> new ActiveMQTextCompatibleMessage(Stub(ClientSession))
            ScFormDataMessage messageOne = new ScFormDataMessage()
            ScFormDataMessage messageTwo = new ScFormDataMessage()

        when:
            jmsLkMessageSender.sendMessages(testSendNotificationRequestDto)
        then:
            1 * jmsTemplate.send(_,_) >> { arguments ->
                MessageCreator mc = arguments[1] as MessageCreator
                Message msg = mc.createMessage(session)

                List<ScFormDataMessage> mp = objectMapper.readValue(msg.getProperties().get("text").toString(), new TypeReference<List<ScFormDataMessage>>(){})
                messageOne = mp.get(0)
                messageTwo = mp.get(1)
            }
            messageOne.getName() == 'f1'
            messageOne.getValue() == 'f2'
            messageOne.getMnemonic() == 'f3'

            messageTwo.getName() == 'k1'
            messageTwo.getValue() == 'k2'
            messageTwo.getMnemonic() == 'k3'
    }

}
