package ru.gosuslugi.pgu.smev.receiver.jms;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.atc.carcass.common.model.Smev3Message;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.dto.kindergarten.KinderGartenRequestDto;
import ru.gosuslugi.pgu.dto.kindergarten.KinderGartenResponseDto;
import ru.gosuslugi.pgu.dto.kindergarten.KinderGartenStatusMessage;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.debug;
import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.error;
import static ru.gosuslugi.pgu.dto.kindergarten.KinderGartenHandlerStatus.SmevError;
import static ru.gosuslugi.pgu.dto.kindergarten.KinderGartenHandlerStatus.TimeoutError;

@Slf4j
@Component
public class SmevJmsMessageSender {

    private static final String ORDER_ID = "orderId";
    /** Время ожидания ответного сообщения. */
    private static final long RECEIVE_TIMEOUT = 60000;

    private final JmsTemplate jmsTemplate;
    private final Destination smevAdapterToDestination;
    private final Destination smevAdapterFromDestination;

    public SmevJmsMessageSender(JmsTemplate jmsTemplate,
                                @Qualifier("smevAdapterToDestination") Destination smevAdapterToDestination,
                                @Qualifier("smevAdapterFromDestination") Destination smevAdapterFromDestination) {
        this.jmsTemplate = jmsTemplate;
        this.jmsTemplate.setReceiveTimeout(RECEIVE_TIMEOUT);
        this.smevAdapterToDestination = smevAdapterToDestination;
        this.smevAdapterFromDestination = smevAdapterFromDestination;
    }

    @Transactional
    public KinderGartenResponseDto sendMessage(KinderGartenRequestDto request) {
        KinderGartenResponseDto responseDto = new KinderGartenResponseDto();
        try {
            String correlationId = UUID.randomUUID().toString();
            jmsTemplate.send(smevAdapterToDestination, session -> createMessage(session, request, correlationId));
            debug(log, () -> String.format("Message sent to %s. orderId=%d, message={%s}", smevAdapterToDestination, request.getOrderId(), request));

            String messageSelector = "JMSCorrelationID = '" + correlationId + '\'';
            Message replyMessage = jmsTemplate.receiveSelected(smevAdapterFromDestination, messageSelector);

            if (replyMessage != null) {
                String replyMessageBody = replyMessage.getBody(String.class);
                debug(log, () -> String.format("Response received from %s. orderId=%d, replyMessage={%s}", smevAdapterFromDestination, request.getOrderId(), replyMessageBody));
                responseDto.setXmlResponse(replyMessageBody);
            } else {
                responseDto.setMessage(new KinderGartenStatusMessage(TimeoutError, String.format("Ответ %s не найден в очереди", messageSelector)));
            }
        } catch (Exception e) {
            error(log, () -> String.format("Cannot receive the kindergarten info from SMEV version %s with orderId %s", request.getSmevVersion(), request.getOrderId()));
            responseDto.setMessage(new KinderGartenStatusMessage(SmevError, ExceptionUtils.getRootCauseMessage(e)));
        }
        return responseDto;
    }

    private Message createMessage(Session session, KinderGartenRequestDto requestDto, String correlationId)
            throws JMSException {
        TextMessage message = session.createTextMessage();
        message.setJMSReplyTo(smevAdapterToDestination);
        Duration connectTimeout = Duration.of(requestDto.getTimeout(), ChronoUnit.MILLIS);
        Smev3Message msg = new Smev3Message()
                .setBody(requestDto.getXmlRequest())
                .setEol(new Date(System.currentTimeMillis() + connectTimeout.toMillis()));
        String smevAdapterMessage = JsonProcessingUtil.toJson(msg);
        message.setText(smevAdapterMessage);
        message.setJMSCorrelationID(correlationId); // генерация id сообщения для поиска ошибок и фильтрации ответа
        message.setStringProperty("messageType", requestDto.getSmevVersion()); //тип сообщения
        message.setStringProperty("sourceInstanceCode", "epgu");
        message.setLongProperty(ORDER_ID, requestDto.getOrderId());
        return message;
    }
}
