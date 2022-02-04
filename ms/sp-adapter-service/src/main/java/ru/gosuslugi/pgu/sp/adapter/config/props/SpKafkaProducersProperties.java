package ru.gosuslugi.pgu.sp.adapter.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaProducerProperties;

@Data
@Component
@ConfigurationProperties("spring.kafka.producers")
public class SpKafkaProducersProperties {

    KafkaProducerProperties suggestions;
    KafkaProducerProperties errors;
    KafkaProducerProperties selfErrors;
    KafkaProducerProperties response;

}
