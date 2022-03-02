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
import ru.gosuslugi.pgu.dto.suggest.SuggestOrderDto;
import ru.gosuslugi.pgu.sp.adapter.config.props.SpKafkaProducersProperties;

/**
 * Настройка обмена сообщениями - suggestions
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.producers.suggestions", name = "enabled", havingValue = "true")
public class KafkaSuggestionsProducerConfig {

    private final KafkaProducerCreator kafkaProducerCreator;
    private final SpKafkaProducersProperties spKafkaProducersProperties;

    @Bean
    @ConditionalOnProperty(prefix = "spring.kafka", name = "auto-create-topics", havingValue = "true")
    public NewTopic suggestionTopic() {
        return spKafkaProducersProperties.getSuggestions().toNewTopic();
    }

    @Bean
    public ProducerFactory<Long, SuggestOrderDto> suggestionProducerFactory() {
        return kafkaProducerCreator.createProducerFactory(new LongSerializer(), new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<Long, SuggestOrderDto> suggestionKafkaTemplate() {
        return kafkaProducerCreator.createKafkaTemplate(suggestionProducerFactory());
    }
}
