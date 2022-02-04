package ru.gosuslugi.pgu.sp.adapter.config;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.gosuslugi.pgu.common.kafka.config.KafkaConsumerCreator;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaConsumerProperties;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.sp.adapter.controller.SmevTemplateBatchKafkaListener;

import java.util.List;

/**
 * Конфигурация для обмена сообщениями с сервисом форм
 */
@Configuration
@RequiredArgsConstructor
public class KafkaFormServiceConsumerConfig {

    private final KafkaConsumerCreator kafkaConsumerCreator;

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.consumers.form-service-batch")
    public KafkaConsumerProperties formServiceBatchConsumerProperties() {
        return new KafkaConsumerProperties();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service-batch", name = "enabled")
    public ConsumerFactory<Long, List<SpAdapterDto>> smevBatchRequestConsumerFactory() {
        return kafkaConsumerCreator.createConsumerFactory(
                new LongDeserializer(),
                new JsonDeserializer<>(new TypeReference<>() {}, false),
                formServiceBatchConsumerProperties()
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service-batch", name = "enabled")
    public ConcurrentMessageListenerContainer<Long, List<SpAdapterDto>> smevBatchRequestListenerContainer(
            SmevTemplateBatchKafkaListener smevTemplateBatchKafkaListener
    ) {
        return kafkaConsumerCreator.createListenerContainer(
                smevBatchRequestConsumerFactory(),
                formServiceBatchConsumerProperties(),
                smevTemplateBatchKafkaListener
        );
    }

}
