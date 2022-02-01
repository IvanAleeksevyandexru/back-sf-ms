package ru.gosuslugi.pgu.pdf.builder;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import ru.gosuslugi.pgu.common.core.util.CloseableList;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ru.gosuslugi.pgu.pdf.builder.ScalingUtil.calcImageSize;

/**
 * Класс позволяет объединить файлы изображений и PDF в 1
 * Каждый файл изображения будет находиться на отдельной странице
 * Изображения - каждый файл будет растянут максимально с учетом ширины и высоты
 * Поддерживаемые типы файлов {@link FileTypes}
 * PDF - добавляется в конец файла
 */
public final class Merger {
    /** Список файлов изображений */
    private final Map<ResourceType,Set<File>> resourceFiles = new HashMap<>();

    /**
     * Добавление файла в список на объединение
     * Поддерживаемый список расширений файлов jpg, jpeg, tif, tiff, gif, png, bmp, pdf {@link FileTypes}
     * @param file файл для добавления в список объединяемых файлов
     * @exception FileExtensionException если данный тип файлов не поддерживается
     */
    public void addResourceFile(File file) throws FileExtensionException {
        ResourceFile resource = new ResourceFile(file);
        resourceFiles.putIfAbsent(resource.type, new HashSet<>());
        resourceFiles.get(resource.type).add(resource.file);
    }

    /**
     * Добавление списка файлов на объединение
     * Поддерживаемый список расширений файлов jpg, jpeg, tif, tiff, gif, png, bmp, pdf
     * @param files список файлов
     * @throws FileExtensionException если в списке содержится файл, который не поддерживается
     */
    public void addResourceFiles(Set<File> files) throws FileExtensionException {
        for (File file : files) {
            addResourceFile(file);
        }
    }

    /**
     * Возвращает список файлов которые будут использованы для создания PDF
     * @return неизменяемый список файлов
     */
    public Set<File> getResourceFilesSet() {
        Set<File> files = new HashSet<>();
        resourceFiles.values().forEach(files::addAll);
        return Collections.unmodifiableSet(files);
    }

    public void build(File pdfFile) throws IOException {
        build(pdfFile, null);
    }

    /**
     * Создает файл PDF который содержит все добавленные ресурсы
     * @param pdfFile файл для записи результата
     */
    public void build(File pdfFile, Comparator<File> comparator) throws IOException {
        try (CloseableList closeableList = new CloseableList()) {
            PDDocument document = closeableList.add(new PDDocument());
            for(File resource : getSortedFiles(ResourceType.IMAGE, comparator))
                addImagePage(resource, document);

            PDFMergerUtility mergerUtility = new PDFMergerUtility();
            for(File resource : getSortedFiles(ResourceType.PDF, comparator)) {
                PDDocument source = closeableList.add(PDDocument.load(resource));
                mergerUtility.appendDocument(document, source);
            }
            document.save(pdfFile);
        }
    }

    private Collection<File> getSortedFiles(ResourceType resourceType, Comparator<File> comparator) {
        Set<File> files = this.resourceFiles.getOrDefault(resourceType, Set.of());
        return comparator == null ? files: files.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Добавление ресурса как изображения в PDF файл
     * @param image файл изображения
     * @param document документ PDF
     * @throws IOException если возникает ошибка при чтении ресурсов
     */
    private void addImagePage(File image, PDDocument document) throws IOException {
        final PDPage page = new PDPage();
        document.addPage(page);
        page.getMediaBox().setLowerLeftX(5);
        page.getMediaBox().setUpperRightX(page.getMediaBox().getWidth() - 5);
        page.getMediaBox().setLowerLeftY(5);
        page.getMediaBox().setUpperRightY(page.getMediaBox().getHeight() - 5);
        final Size boxSize = new Size(page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
        final PDImageXObject imgObject = PDImageXObject.createFromFile(image.getPath(), document);
        final Size imageSize = new Size(imgObject.getWidth(), imgObject.getHeight());
        final Size resultImageSize = calcImageSize(imageSize, boxSize);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(
                    imgObject,
                    5 + ((boxSize.width - resultImageSize.width) / 2),
                    5 + (boxSize.height - resultImageSize.height),
                    resultImageSize.width,
                    resultImageSize.height
            );
        }
    }
}
