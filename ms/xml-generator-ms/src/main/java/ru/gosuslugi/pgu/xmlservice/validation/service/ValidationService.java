package ru.gosuslugi.pgu.xmlservice.validation.service;

/**
 * Validates input.
 */
public interface ValidationService<T> {

    /**
     * Method validates input.
     *
     * @param input input object.
     * @throws ru.gosuslugi.pgu.xmlservice.validation.exception.ValidationException if input is not
     * valid
     */
    void validate(T input);

}
