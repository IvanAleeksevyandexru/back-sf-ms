package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.sp.adapter.data.SmevRequest;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

import java.util.Collections;

/**
 * Сервис взаимодействия со СМЭВ
 */
public interface ServiceProcessingClient {

    /**
     * Вызов sp адаптера
     * @param smevRequest - контекст вызова
     */
    void orderCall(SmevRequest smevRequest);
}
