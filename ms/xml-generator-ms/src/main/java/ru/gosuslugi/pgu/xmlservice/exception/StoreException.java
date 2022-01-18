package ru.gosuslugi.pgu.xmlservice.exception;

import ru.gosuslugi.pgu.fs.common.exception.FormBaseException;

/**
 * Ошибка сохранения файла.
 */
public class StoreException extends FormBaseException {
    public StoreException(String s) {
        super(s);
    }
}
