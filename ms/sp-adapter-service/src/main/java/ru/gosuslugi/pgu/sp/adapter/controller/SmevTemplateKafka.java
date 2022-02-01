package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.kafka.service.KafkaBatchProcessor;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.sp.adapter.properties.SpKafkaConsumersProperties;

import java.util.List;

/**
 * Обмен сообщениями с сервисом форм
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service", name = "enabled", havingValue = "true")
public class SmevTemplateKafka {

    private final SmevTemplateMessageProcessor smevTemplateMessageProcessor;
    private final KafkaBatchProcessor kafkaBatchProcessor;
    private final SpKafkaConsumersProperties spKafkaConsumersProperties;

    /**
     * Запуск процесса генерации файлов и вызова SP
     */
    @KafkaListener(
        topics = "#{spKafkaConsumersProperties.formService.topic}",
        groupId = "#{spKafkaConsumersProperties.formService.groupId}",
        concurrency = "#{spKafkaConsumersProperties.formService.concurrency}",
        containerFactory = "smevRequestKafkaListenerContainerFactory"
    )
    public void sendSmevRequest(List<Message<SpAdapterDto>> messages) {
        log.info("Принято из kafka очереди {} {} сообщений", spKafkaConsumersProperties.getFormService().getTopic(), messages.size());
        kafkaBatchProcessor.process(messages, smevTemplateMessageProcessor::processMessage);
    }

}
