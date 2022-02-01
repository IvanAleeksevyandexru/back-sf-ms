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

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service-batch", name = "enabled", havingValue = "true")
public class SmevTemplateBatchKafka {

    private final SmevTemplateMessageProcessor smevTemplateMessageProcessor;
    private final KafkaBatchProcessor kafkaBatchProcessor;
    private final SpKafkaConsumersProperties spKafkaConsumersProperties;

    @KafkaListener(
        topics = "#{spKafkaConsumersProperties.formServiceBatch.topic}",
        groupId = "#{spKafkaConsumersProperties.formServiceBatch.groupId}",
        concurrency = "#{spKafkaConsumersProperties.formServiceBatch.concurrency}",
        containerFactory = "smevBatchRequestKafkaListenerContainerFactory"
    )
    public void sendBatchSmevRequest(List<Message<List<SpAdapterDto>>> messages) {
        log.info("Принято из пакетной kafka очереди {} {} сообщений", spKafkaConsumersProperties.getFormServiceBatch().getTopic(), messages.size());
        kafkaBatchProcessor.process(messages, list -> list.forEach(smevTemplateMessageProcessor::processMessage));
    }

}
