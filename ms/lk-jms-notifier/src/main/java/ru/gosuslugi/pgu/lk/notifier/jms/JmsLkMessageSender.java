package ru.gosuslugi.pgu.lk.notifier.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.gosuslugi.pgu.common.core.exception.JsonParsingException;
import ru.gosuslugi.pgu.dto.lk.SendNotificationRequestDto;
import ru.gosuslugi.pgu.lk.notifier.model.dto.ScFormDataMessage;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmsLkMessageSender {

    private static final String ORDER_ID = "orderId";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final Destination lkNotifyDestination;

    @Transactional
    public void sendMessages(SendNotificationRequestDto request) {
        List<ScFormDataMessage> messages = request.getMessages().stream().
                map(message -> new ScFormDataMessage(message.getFieldName(), message.getFieldValue(), message.getFieldMnemonic()))
                .collect(toList());
        try {
            jmsTemplate.send(lkNotifyDestination, session -> createMessage(session, request.getOrderId(), messages));
            if (log.isDebugEnabled()) {
                log.debug("Notification sent to {}. orderId={}, messages={}", jmsTemplate.getDefaultDestinationName(), request.getOrderId(), messages);
            }
        } catch (Exception e) {
            log.error("JMS exception while sending notification to LK for request: {}", request, e);
        }
    }

    private Message createMessage(Session session, Long orderId, List<ScFormDataMessage> messages)
            throws JMSException {
        TextMessage message = session.createTextMessage();

        message.setLongProperty(ORDER_ID, orderId);
        message.setText(messagesToString(messages));

        return message;
    }

    private String messagesToString(List<ScFormDataMessage> messages) {
        try {
            return objectMapper.writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("Error processing object: " + messages, e);
        }
    }


}
