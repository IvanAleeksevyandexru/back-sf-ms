package ru.gosuslugi.pgu.pdf.builder;

/**
 * Ошибка возникает если файл для склейки не имеет неверное расширение
 */
public final class FileExtensionException extends Exception {
    /**
     * Инициализация ошибки расширения файла с указанием сообщения
     * @param message сообщение об ошибке
     */
    public FileExtensionException(String message) {
        super(message);
    }
}
