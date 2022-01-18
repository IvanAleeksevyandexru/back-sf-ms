package ru.gosuslugi.pgu.sp.adapter.service.suggestion;

import java.util.concurrent.ExecutionException;

/**
 * Сервис для нотификации Suggestion Service о готовонсти черновика к отправке в SP. Используется для сохранения полей черновика в базу.
 * Как траспорт используется Kafka.
 */
public interface SuggestionServiceNotifier {

    void send(Long userId, Long orderId);

}
