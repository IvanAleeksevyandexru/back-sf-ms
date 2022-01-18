package ru.gosuslugi.pgu.sp.adapter.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class SendResultListener implements ListenableFutureCallback<SendResult<Long, String>> {

    private final Long orderId;

    public SendResultListener(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public void onSuccess(SendResult<Long, String> result) {
        if (result == null) {
            log.error("Analytics kafka sending error: result is null for orderId {}", orderId);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Analytics kafka: sent draft with orderId {} and offset=[{}]", orderId, result.getRecordMetadata().offset());
        }
    }

    @Override
    public void onFailure(Throwable ex) {
        log.error("Analytics kafka: unable to send draft with orderId {} due to: {}", orderId, ex.getMessage(), ex);
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        } else {
            throw new RuntimeException(ex);
        }
    }
}