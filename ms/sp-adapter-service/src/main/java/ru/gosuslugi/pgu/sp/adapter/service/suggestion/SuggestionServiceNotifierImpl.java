package ru.gosuslugi.pgu.sp.adapter.service.suggestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import ru.gosuslugi.pgu.dto.suggest.SuggestOrderDto;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaProducerProperties;

import static java.lang.String.format;
import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.debug;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.suggestions", name = "enabled", havingValue = "true")
public class SuggestionServiceNotifierImpl implements SuggestionServiceNotifier {

    private final KafkaProducerProperties kafkaSuggestionsProperties;
    private final KafkaTemplate<Long, SuggestOrderDto> suggestionKafkaTemplate;

    @Override
    public void send(Long userId, Long orderId) {
        if (userId < 0) { // Для созаявителей создается копия основного заявления, а userId фейковый: -10000, -10001 и т.д.
            return;
        }
        debug(log, () -> format("Sending notification to suggestion service to save suggests for user %s and order %s", userId, orderId));

        var dto = new SuggestOrderDto();
        dto.setOrderId(orderId);
        dto.setUserId(userId);

        ListenableFuture<SendResult<Long, SuggestOrderDto>> future =
                suggestionKafkaTemplate.send(kafkaSuggestionsProperties.getTargetTopic().getTopicName(), userId, dto);
        future.addCallback(new SendResultListener(userId, dto));
    }
}
