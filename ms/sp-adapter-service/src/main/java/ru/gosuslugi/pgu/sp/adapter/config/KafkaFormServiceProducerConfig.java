package ru.gosuslugi.pgu.sp.adapter.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gosuslugi.pgu.common.kafka.config.KafkaProducerCreator;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;
import ru.gosuslugi.pgu.dto.SpResponseOkDto;
import ru.gosuslugi.pgu.sp.adapter.config.props.SpKafkaProducersProperties;

/**
 * Конфигурация для обмена сообщениями с сервисом форм
 */
@Configuration
@RequiredArgsConstructor
public class KafkaFormServiceProducerConfig {

    private final SpKafkaProducersProperties spKafkaProducersProperties;

    private final KafkaProducerCreator kafkaProducerCreator;

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    public NewTopic selfErrorsTopic() {
        return spKafkaProducersProperties.getSelfErrors().toNewTopic();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    public NewTopic extErrorTopic() {
        return spKafkaProducersProperties.getErrors().toNewTopic();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    public NewTopic spResponseTopic() {
        return spKafkaProducersProperties.getResponse().toNewTopic();
    }

    @Bean
    public ProducerFactory<Long, SpRequestErrorDto> spErrorsProducerFactory() {
        return kafkaProducerCreator.createProducerFactory(new LongSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<Long, SpRequestErrorDto> spErrorsTemplate() {
        return kafkaProducerCreator.createKafkaTemplate(spErrorsProducerFactory());
    }

    @Bean
    public ProducerFactory<Long, SpResponseOkDto> spResponseProducerFactory() {
        return kafkaProducerCreator.createProducerFactory(new LongSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<Long, SpResponseOkDto> spResponseTemplate() {
        return kafkaProducerCreator.createKafkaTemplate(spResponseProducerFactory());
    }

}
