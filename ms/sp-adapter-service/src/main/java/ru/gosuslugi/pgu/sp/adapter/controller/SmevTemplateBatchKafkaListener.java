package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaConsumerProperties;
import ru.gosuslugi.pgu.common.kafka.service.AbstractBatchMessageListener;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;
import ru.gosuslugi.pgu.sp.adapter.config.props.SpKafkaProducersProperties;

import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service-batch", name = "enabled", havingValue = "true")
public class SmevTemplateBatchKafkaListener extends AbstractBatchMessageListener<Long, List<SpAdapterDto>> {

    private final SmevTemplateMessageProcessor smevTemplateMessageProcessor;
    private final KafkaTemplate<Long, SpRequestErrorDto> errorTopicTemplate;
    private final SpKafkaProducersProperties spKafkaProducersProperties;


    public SmevTemplateBatchKafkaListener(SmevTemplateMessageProcessor smevTemplateMessageProcessor,
                                          KafkaConsumerProperties formServiceBatchConsumerProperties,
                                          SpanService spanService,
                                          KafkaTemplate<Long, SpRequestErrorDto> errorTopicTemplate,
                                          SpKafkaProducersProperties spKafkaProducersProperties) {
        super(formServiceBatchConsumerProperties, spanService);
        this.smevTemplateMessageProcessor = smevTemplateMessageProcessor;
        this.errorTopicTemplate = errorTopicTemplate;
        this.spKafkaProducersProperties = spKafkaProducersProperties;
        this.onErrorCallback = this::processError;
    }

    @Override
    protected void processMessage(List<SpAdapterDto> messages) {
        messages.forEach(smevTemplateMessageProcessor::processMessage);
    }

    private void processError(List<SpAdapterDto> spAdapterDtoList, String cause) {
        for (SpAdapterDto spAdapterDto: spAdapterDtoList) {
            SpRequestErrorDto spError = new SpRequestErrorDto();
            spError.setAdapterRequestDto(spAdapterDto);
            spError.setCause(cause);
            errorTopicTemplate.send(spKafkaProducersProperties.getSelfErrors().getTopic(), spError);
        }

    }
}
