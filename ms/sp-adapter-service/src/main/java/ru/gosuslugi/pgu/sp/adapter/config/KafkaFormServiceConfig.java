package ru.gosuslugi.pgu.sp.adapter.config;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gosuslugi.pgu.common.kafka.config.ConsumerBeansConfigurer;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaConsumerProperties;
import ru.gosuslugi.pgu.common.kafka.util.KafkaFactoryUtils;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;
import ru.gosuslugi.pgu.dto.SpResponseOkDto;
import ru.gosuslugi.pgu.sp.adapter.properties.SpKafkaConsumersProperties;
import ru.gosuslugi.pgu.sp.adapter.properties.SpKafkaProducersProperties;

import java.util.List;
import java.util.Map;

/**
 * Конфигурация для обмена сообщениями с сервисом форм
 */
@Configuration
@RequiredArgsConstructor
public class KafkaFormServiceConfig {

    @Value(value = "${spring.kafka.brokers}")
    private String brokers;

    private final SpKafkaProducersProperties spKafkaProducersProperties;
    private final SpKafkaConsumersProperties spKafkaConsumersProperties;

    @Bean
    public ProducerFactory<Long, SpRequestErrorDto> spAdapterErrorsProducer() {
        return KafkaFactoryUtils.createDefaultProducerFactory(brokers, new LongSerializer(), new JsonSerializer<>());
    }

    @Bean
    public ProducerFactory<Long, SpResponseOkDto> spAdapterResponseProducer() {
        return KafkaFactoryUtils.createDefaultProducerFactory(brokers, new LongSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<Long, SpRequestErrorDto> spErrorsTemplate() {
        return new KafkaTemplate<>(spAdapterErrorsProducer());
    }

    @Bean
    public KafkaTemplate<Long, SpResponseOkDto> spResponseTemplate() {
        return new KafkaTemplate<>(spAdapterResponseProducer());
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service", name = "enabled")
    public ConcurrentKafkaListenerContainerFactory<Long, SpAdapterDto> smevRequestKafkaListenerContainerFactory() {
        return createContainerFactory(
            new LongDeserializer(),
            new JsonDeserializer<>(SpAdapterDto.class, false),
            spKafkaConsumersProperties.getFormService()
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka.consumers.form-service-batch", name = "enabled")
    public ConcurrentKafkaListenerContainerFactory<Long, List<SpAdapterDto>> smevBatchRequestKafkaListenerContainerFactory() {
        return createContainerFactory(
            new LongDeserializer(),
            new JsonDeserializer<>(new TypeReference<>() {}, false),
            spKafkaConsumersProperties.getFormServiceBatch()
        );
    }

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

    private <K, V> ConcurrentKafkaListenerContainerFactory<K, V> createContainerFactory(
        Deserializer<K> keyDeserializer,
        Deserializer<V> valueDeserializer,
        KafkaConsumerProperties consumerProperties
    ) {
        ConcurrentKafkaListenerContainerFactory<K, V> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory(keyDeserializer, valueDeserializer, consumerProperties));
        ConsumerBeansConfigurer.configureContainerFactory(factory, consumerProperties);
        return factory;
    }

    private <K, V> ConsumerFactory<K, V> createConsumerFactory(
        Deserializer<K> keyDeserializer,
        Deserializer<V> valueDeserializer,
        KafkaConsumerProperties consumerProperties
    ) {
        Map<String, Object> consumerProps = ConsumerBeansConfigurer.createProps(brokers, consumerProperties);
        return new DefaultKafkaConsumerFactory<>(
            consumerProps,
            new ErrorHandlingDeserializer<>(keyDeserializer),
            new ErrorHandlingDeserializer<>(valueDeserializer)
        );
    }

}
