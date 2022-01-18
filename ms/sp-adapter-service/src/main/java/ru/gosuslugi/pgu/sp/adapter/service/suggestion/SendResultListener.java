package ru.gosuslugi.pgu.sp.adapter.service.suggestion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.gosuslugi.pgu.dto.suggest.SuggestOrderDto;

import static java.lang.String.format;
import static ru.gosuslugi.pgu.common.core.logger.LoggerUtil.debug;

@Slf4j
public class SendResultListener implements ListenableFutureCallback<SendResult<Long, SuggestOrderDto>> {

    private final Long userId;
    private final SuggestOrderDto dto;

    public SendResultListener(Long userId, SuggestOrderDto dto) {
        this.userId = userId;
        this.dto = dto;
    }

    @Override
    public void onSuccess(SendResult<Long, SuggestOrderDto> result) {
        if (result == null) {
            log.error("Kafka sending error: result is null for user {} and orderId {}", userId, dto);
            return;
        }

        debug(log, () -> format("Notification to suggestion service sent successfully for user %s, order %s and offset=[%s]",
                userId, dto.getOrderId(), result.getRecordMetadata().offset()));
    }

    @Override
    public void onFailure(Throwable ex) {
        log.error("Suggestion service:unable to send draft with user {} and orderId {} due to: {}", userId, dto.getOrderId(), ex.getMessage(), ex);
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        } else {
            throw new RuntimeException(ex);
        }
    }
}
