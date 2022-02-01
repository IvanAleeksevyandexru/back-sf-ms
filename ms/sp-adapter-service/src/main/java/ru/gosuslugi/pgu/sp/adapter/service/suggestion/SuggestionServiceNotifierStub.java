package ru.gosuslugi.pgu.sp.adapter.service.suggestion;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "spring.kafka.producers.suggestions", name = "enabled", havingValue = "false", matchIfMissing = true)
public class SuggestionServiceNotifierStub implements SuggestionServiceNotifier {
    @Override
    public void send(Long userId, Long orderId) { }
}
