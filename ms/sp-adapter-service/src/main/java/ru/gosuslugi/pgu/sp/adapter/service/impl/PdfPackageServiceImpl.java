package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.dto.PdfFilePackage;
import ru.gosuslugi.pgu.dto.TerrabyteFileInfo;
import ru.gosuslugi.pgu.pdf.builder.FileExtensionException;
import ru.gosuslugi.pgu.pdf.builder.FilesComparatorFactory;
import ru.gosuslugi.pgu.pdf.builder.Merger;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.exceptions.PdfGenerateException;
import ru.gosuslugi.pgu.sp.adapter.service.PdfPackageService;
import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис упаковки файлов в PDF
 */
@Service
@AllArgsConstructor
@Slf4j
public class PdfPackageServiceImpl implements PdfPackageService {
    private static final String PDF_MIME_TYPE = "application/pdf";

    private final TerrabyteClient terrabyteClient;
    private final AttachmentService attachmentService;

    /**
     * Упаковка файлов в PDF
     * @param context context sp-adapter
     */
    @Override
    public void packageToPdf(TemplatesDataContext context) {
        if (context.getPackageToPdf() == null)
            return;

        val orderId = context.getOrderId();
        //Все файлы для объединения

        for (PdfFilePackage pdfPack : context.getPackageToPdf()) {
            try {
                packing(pdfPack, context);
            } catch (IOException | FileExtensionException e) {
                String error = String.format(
                        "Ошибка формирования файла PDF из вложений orderId = %d, filename = %s",
                        orderId,
                        pdfPack.getFilename()
                );
                log.error(error, e);
                throw new PdfGenerateException(error, e);
            }
        }
    }

    /**
     * Упаковка конкретного пакета
     * @param pdfPack пакет упаковки
     * @param context sp-adapter контекст
     * @throws IOException при ошибках работы с файлами
     * @throws FileExtensionException при генерации PDF из файла с недопустимым расширением
     */
    private void packing(PdfFilePackage pdfPack, TemplatesDataContext context) throws IOException, FileExtensionException {
        //скачивание
        val files = pdfPack.getFileInfos()
                .parallelStream()
                .map(this::getFileFromTerrabyte)
                .collect(Collectors.toSet());
        // объединение
        File pdfFile;
        if (files.isEmpty()) return;
        try {
            Comparator<File> comparator = FilesComparatorFactory.getComparator(context.getServiceId());
            pdfFile = merge(pdfPack.getFilename(), files, comparator);
        } catch (Exception e) {
            log.error("Ошибка генерации файла {}", pdfPack.getFilename(), e);
            deleteFiles(files);
            throw new PdfGenerateException(String.format("Ошибка генерации файла %s", pdfPack.getFilename()), e);
        }
        // сохранение
        try {
            byte[] fileBody = Files.readAllBytes(pdfFile.toPath());
            attachmentService.saveAttachment(
                    context.getOrderId(),
                    PDF_MIME_TYPE,
                    pdfPack.getFilename(),
                    pdfPack.getFilename(),
                    fileBody,
                    context.getAttachments(),
                    context.getGeneratedFiles());
        } catch (Exception e) {
            String errorInfo = String.format("Ошибка генерации файла %s", pdfPack.getFilename());
            log.error(errorInfo, e);
            throw new PdfGenerateException(errorInfo, e);
        }
        // удаление
        files.add(pdfFile);
        deleteFiles(files);
    }

    /**
     * Удаление временных файлов
     * @param files список файлов на удаление
     */
    private void deleteFiles(Set<File> files) {
        for (val file : files)
            if (!file.delete())
                log.warn("Ошибка удаления файла {}", file.getAbsolutePath());
    }

    /**
     * Скачивание файлов из terrabyte, сохранение во временной директории
     * @param fInfo информация о файле
     * @return файл скаченный из терабайт
     */
    private File getFileFromTerrabyte(TerrabyteFileInfo fInfo) {
        try {
            val file = File.createTempFile(String.format("uid_%d", fInfo.getUid()), "." + fInfo.getFileExt());
            val bytes = terrabyteClient.getFile(fInfo.getUid());
            FileUtils.writeByteArrayToFile(file, bytes);
            return file;
        } catch (IOException ioe) {
            String error = String.format("Ошибка скачивания файла из терабайта fileUid = %d", fInfo.getUid());
            log.error(error, ioe);
            throw new PdfGenerateException(error, ioe);
        }
    }

    /**
     * Сборка списка файлов в PDF файл
     * @param fileName название файла
     * @param files список файлов для упаковки
     * @return файл сгенерированной PDF
     * @throws IOException при ошибках работы с файлами
     * @throws FileExtensionException при генерации PDF из файла с недопустимым расширением
     */
    private File merge(String fileName, Set<File> files, Comparator<File> comparator) throws IOException, FileExtensionException {
        val merger = new Merger();
        merger.addResourceFiles(files);
        val tmp = File.createTempFile(fileName, ".pdf");
        merger.build(tmp, comparator);
        return tmp;
    }
}
