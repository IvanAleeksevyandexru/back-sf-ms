package ru.gosuslugi.pgu.sp.adapter.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gosuslugi.pgu.common.kafka.config.ConsumerBeansConfigurer;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaConsumerProperties;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;
import ru.gosuslugi.pgu.dto.SpResponseOkDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация для обмена сообщениями с сервисом форм
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.form-service", name = "enabled", havingValue = "true")
public class KafkaFormServiceConfig {

    @Value(value = "${spring.kafka.brokers}")
    private String brokers;

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.form-service")
    public KafkaConsumerProperties kafkaFormServiceProperties() {
        return new KafkaConsumerProperties();
    }

    @Bean
    public ProducerFactory<Long, SpRequestErrorDto> spAdapterErrorsProducer() {
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    @Bean
    public ProducerFactory<Long, SpResponseOkDto> spAdapterResponseProducer() {
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    @Bean
    public ConsumerFactory<Long, SpAdapterDto> spAdapterDtoConsumerFactory() {
        KafkaConsumerProperties properties = kafkaFormServiceProperties();
        Map<String, Object> consumerProps = ConsumerBeansConfigurer.createProps(brokers, properties);

        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(consumerProps,
                new ErrorHandlingDeserializer<>(new LongDeserializer()),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(SpAdapterDto.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, SpAdapterDto> smevRequestKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, SpAdapterDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(spAdapterDtoConsumerFactory());

        ConsumerBeansConfigurer.configureContainerFactory(factory, kafkaFormServiceProperties());

        return factory;
    }


    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    @Bean
    public NewTopic selfErrorsTopic() {
        KafkaConsumerProperties properties = kafkaFormServiceProperties();
        return new NewTopic(
                properties.getSelfErrorsTopicName(),
                properties.getSelfErrorsPartitions(),
                properties.getSelfErrorsReplicationFactor()
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    public NewTopic extErrorTopic() {
        KafkaConsumerProperties properties = kafkaFormServiceProperties();
        return new NewTopic(
                properties.getErrorsTopicName(),
                properties.getErrorsPartitions(),
                properties.getErrorsReplicationFactor()
        );
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    public NewTopic spResponseTopic() {
        KafkaConsumerProperties properties = kafkaFormServiceProperties();
        return new NewTopic(
                properties.getResponseTopicName(),
                properties.getResponsePartitions(),
                properties.getResponseReplicationFactor()
        );
    }

    @Bean
    public KafkaTemplate<Long, SpRequestErrorDto> spErrorsTemplate() {
        return new KafkaTemplate<>(spAdapterErrorsProducer());
    }

    @Bean
    public KafkaTemplate<Long, SpResponseOkDto> spResponseTemplate() {
        return new KafkaTemplate<>(spAdapterResponseProducer());
    }

    private Map<String, Object> producerProperties() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }
}
