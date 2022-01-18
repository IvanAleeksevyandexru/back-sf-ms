package ru.gosuslugi.pgu.pdf.template.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.pdf.template.model.data.TemplatesDataContext;

@UtilityClass
public class FileNameUtil {

    private final static String XML_EXTENSION = "xml";
    private final static String PDF_EXTENSION = "pdf";

    private static final String PATH_SEPARATOR = "/";
    private static final String FILENAME_DELIMITER = "_";

    public static String buildAttachmentFileName(TemplatesDataContext templatesDataContext, FileDescription fileDescription) {
        String fileName = fileDescription.getFileName();

        String fileNameWithoutExt = fileName;
        String fileExtension = FilenameUtils.getExtension(fileName);
        if (fileExtension.equals(XML_EXTENSION) || fileExtension.equals(PDF_EXTENSION))
            fileNameWithoutExt = FilenameUtils.getBaseName(fileName);

        String newfileExtension = getFileExtension(fileDescription.getType(), fileDescription.isExtensionDisplay());
        switch(fileDescription.getAddedFileName()) {
            case NONE: return fileNameWithoutExt + "." + newfileExtension;
            case GUID: return fileNameWithoutExt + "_" + templatesDataContext.getRequestGuid() + "." + newfileExtension;
            case HASH: return fileNameWithoutExt + "_" + templatesDataContext.getRequestHash() + "." + newfileExtension;
        }
        return fileName;
    }

    public static String buildAttachmentMnemonic(TemplatesDataContext templatesDataContext, FileDescription fileDescription) {
        String mnemonic = fileDescription.getMnemonic();

        switch(fileDescription.getAddedMnemonic()) {
            case GUID: return mnemonic + "_" + templatesDataContext.getRequestGuid();
            case HASH: return mnemonic + "_" + templatesDataContext.getRequestHash();
        }
        return mnemonic == null ? buildAttachmentFileName(templatesDataContext, fileDescription) : mnemonic;
    }

    private String getFileExtension(FileType fileType, boolean isFileExtension) {
        if (!isFileExtension)
            return "";

        switch(fileType) {
            case REQUEST:
            case XML: return XML_EXTENSION;
            case COMMON_PDF:
            case PDF: return PDF_EXTENSION;
        }
        return "";
    }

    public static String determineTemplateFileName(String baseFolder, String serviceId, ApplicantRole role, String prefix) {
        return baseFolder
                + PATH_SEPARATOR
                + (((prefix == null) ? "" : (prefix + FILENAME_DELIMITER))
                + serviceId
                + ((role == null) ? "" : FILENAME_DELIMITER + role));
    }

}
