package ru.gosuslugi.pgu.draftconverter.context.service;

/**
 * Обрабатывает строку в T.
 */
public interface ParseService<T> {
    /**
     * Обрабатывает строку в объект.
     *
     * @param input входной XML.
     * @return объект.
     */
    T parse(String input);
}
