package ru.gosuslugi.pgu.pdf.builder;

import java.io.File;
import java.util.Arrays;

import static ru.gosuslugi.pgu.pdf.builder.FileUtils.getExtension;

/**
 * Файл описания объединяемых ресурсов
 */
final class ResourceFile {
    /** Файл ресурса */
    final File file;
    /** Тип ресурса */
    final ResourceType type;

    /**
     * Создание ресурса с определением его типа
     * @param file файл ресурса
     * @throws FileExtensionException при обнаружении недопустимого расширения файла
     */
    public ResourceFile(File file) throws FileExtensionException {
        this.file = file;

        final String fileExtensionStr = getExtension(file.getName());
        if (fileExtensionStr == null || notAllowFileExtension(fileExtensionStr))
            throw new FileExtensionException(String.format("Не поддерживается расширение файла - %s", file.getName()));

        if (fileExtensionStr.equalsIgnoreCase("PDF"))
            this.type = ResourceType.PDF;
        else this.type = ResourceType.IMAGE;
    }

    /**
     * Проверка файла на список разрешенных расширений {@link FileTypes}
     * @param filename название файла для проверки
     * @return результат проверки
     */
    private boolean notAllowFileExtension(String filename) {
        return Arrays.stream(FileTypes.values())
                .map(Enum::name)
                .noneMatch(ext -> ext.equalsIgnoreCase(filename));
    }
}
