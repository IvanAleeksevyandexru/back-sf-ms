package ru.gosuslugi.pgu.pdf.builder;

/**
 * Утилитарный класс для работы с файлами
 */
final class FileUtils {
    private FileUtils() { }

    /**
     * Определение расширения файла
     * @param filename имя файла
     * @return расширение файла если оно есть или null если файл не содержит расширения
     */
    public static String getExtension(String filename) {
        if (!filename.contains(".")) return null;
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
