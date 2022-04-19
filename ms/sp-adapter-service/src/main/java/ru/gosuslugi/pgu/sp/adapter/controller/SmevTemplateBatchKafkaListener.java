package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaConsumerProperties;
import ru.gosuslugi.pgu.common.kafka.service.AbstractBatchMessageListener;
import ru.gosuslugi.pgu.common.kafka.service.KafkaRetryService;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.dto.SpAdapterDto;

import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service-batch", name = "enabled", havingValue = "true")
public class SmevTemplateBatchKafkaListener extends AbstractBatchMessageListener<Long, List<SpAdapterDto>> {

    private final SmevTemplateMessageProcessor smevTemplateMessageProcessor;

    public SmevTemplateBatchKafkaListener(SmevTemplateMessageProcessor smevTemplateMessageProcessor,
                                          KafkaConsumerProperties formServiceBatchConsumerProperties,
                                          KafkaRetryService kafkaRetryService,
                                          SpanService spanService) {
        super(formServiceBatchConsumerProperties, kafkaRetryService, spanService);
        this.smevTemplateMessageProcessor = smevTemplateMessageProcessor;
    }

    @Override
    protected void processMessage(List<SpAdapterDto> messages) {
        messages.forEach(smevTemplateMessageProcessor::processMessage);
    }
}
