package ru.gosuslugi.pgu.sp.adapter.service;

import org.apache.commons.io.FilenameUtils;

import ru.gosuslugi.pgu.dto.pdf.data.AttachmentType;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractSmevFileService {

    private static final Set<AttachmentType> SEND_SMEV_FORBIDDEN_TYPES =
            Stream.of(AttachmentType.REQUEST, AttachmentType.SEND_SMEV_FORBIDDEN)
                  .collect(Collectors.toUnmodifiableSet());
    private final String XML_EXTENSION = "xml";
    private final String PDF_EXTENSION = "pdf";


    protected String buildAttachmentFileName(TemplatesDataContext templatesDataContext, FileDescription fileDescription) {
        String fileName = fileDescription.getFileName();

        String fileNameWithoutExt = fileName;
        String fileExtension = FilenameUtils.getExtension(fileName);
        if (fileExtension.equals(XML_EXTENSION) || fileExtension.equals(PDF_EXTENSION))
            fileNameWithoutExt = FilenameUtils.getBaseName(fileName);

        String newfileExtension = getFileExtension(fileDescription.getType(), fileDescription.isExtensionDisplay());
        switch (fileDescription.getAddedFileName()) {
            case NONE:
                return fileNameWithoutExt + newfileExtension;
            case GUID:
                return fileNameWithoutExt + "_" + templatesDataContext.getRequestGuid() + newfileExtension;
            case HASH:
                return fileNameWithoutExt + "_" + templatesDataContext.getRequestHash() + newfileExtension;
        }

        return fileName;
    }

    protected String buildAttachmentMnemonic(TemplatesDataContext templatesDataContext, FileDescription fileDescription) {
        String mnemonic = fileDescription.getMnemonic();

        switch(fileDescription.getAddedMnemonic()) {
            case GUID: return mnemonic + "_" + templatesDataContext.getRequestGuid();
            case HASH: return mnemonic + "_" + templatesDataContext.getRequestHash();
        }
        return mnemonic == null ? buildAttachmentFileName(templatesDataContext, fileDescription) : mnemonic;
    }

    /**
     * Определяет по attachmentType, следует ли отправлять файл как вложение в запросе в СМЭВ.
     *
     * @param attachmentType способ обработки файла как вложения.
     * @return true, если отправка в СМЭВ разрешена.
     */
    protected boolean isSendToSmevAllowed(final AttachmentType attachmentType) {
        return !SEND_SMEV_FORBIDDEN_TYPES.contains(attachmentType);
    }

    private String getFileExtension(FileType fileType, boolean isFileExtension) {
        if (!isFileExtension)
            return "";

        switch(fileType) {
            case REQUEST:
            case XML: return "." + XML_EXTENSION;
            case COMMON_PDF:
            case PDF: return "." + PDF_EXTENSION;
        }
        return "";
    }

}
