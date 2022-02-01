package ru.gosuslugi.pgu.sp.adapter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaConsumerProperties;

@Data
@Component
@ConfigurationProperties("spring.kafka.consumers")
public class SpKafkaConsumersProperties {

    KafkaConsumerProperties formService;
    KafkaConsumerProperties formServiceBatch;

}
