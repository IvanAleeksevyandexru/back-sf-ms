package ru.gosuslugi.pgu.sp.adapter.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaProducerProperties;
import ru.gosuslugi.pgu.dto.suggest.SuggestOrderDto;

import java.util.HashMap;
import java.util.Map;

/**
 * Настройка обмена сообщениями - suggestions
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.suggestions", name = "enabled", havingValue = "true")
public class KafkaSuggestionsConfig {

    @Value(value = "${spring.kafka.brokers}")
    private String brokers;

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.suggestions")
    public KafkaProducerProperties kafkaSuggestionsProperties() {
        return new KafkaProducerProperties();
    }

    @Bean
    public ProducerFactory<Long, SuggestOrderDto> suggestionProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    public NewTopic suggestionTopic() {
        KafkaProducerProperties properties = kafkaSuggestionsProperties();
        return new NewTopic(
                properties.getTargetTopic().getTopicName(),
                properties.getTargetTopic().getTopicPartitions(),
                properties.getTargetTopic().getTopicReplicationFactor()
        );
    }

    @Bean
    public KafkaTemplate<Long, SuggestOrderDto> suggestionKafkaTemplate() {
        return new KafkaTemplate<>(suggestionProducerFactory());
    }
}
