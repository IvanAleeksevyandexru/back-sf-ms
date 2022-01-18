package ru.gosuslugi.pgu.xmlservice.storing.service.impl;

import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.storing.service.FileNamingStrategy;

import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

/**
 * Составляет имя файла и мнемонику на основе описания файла и данных черновика.
 */
@Service
public class FileDescriptionNamingStrategy implements FileNamingStrategy {
    private static final String XML_EXTENSION = "xml";

    @Override
    public String computeFileName(TemplateDataContext dataContext) {
        FileDescription fileDescription = dataContext.getFileDescription();
        String fileName = fileDescription.getFileName();

        String fileNameWithoutExt = stripXmlExtension(fileName);

        String newFileExtension = computeFileExtension(fileDescription.getType());
        switch (fileDescription.getAddedFileName()) {
            case NONE:
                return fileNameWithoutExt + "." + newFileExtension;
            case GUID:
                return fileNameWithoutExt + "_" + computeRequestGuid(dataContext) + "."
                        + newFileExtension;
            case HASH:
                return fileNameWithoutExt + "_" + computeRequestHash(dataContext) + "."
                        + newFileExtension;
        }
        return fileName;
    }

    @Override
    public String computeMnemonic(TemplateDataContext dataContext) {
        FileDescription fileDescription = dataContext.getFileDescription();
        String mnemonic = fileDescription.getMnemonic();

        switch (fileDescription.getAddedMnemonic()) {
            case GUID:
                return mnemonic + "_" + computeRequestGuid(dataContext);
            case HASH:
                return mnemonic + "_" + computeRequestHash(dataContext);
        }
        return mnemonic == null ? computeFileName(dataContext) : mnemonic;
    }

    public String computeRequestGuid(TemplateDataContext dataContext) {
        Object guid =
                dataContext.getAdditionalValues().get(TemplateDataContext.SP_REQUEST_GUID_KEY);
        return Objects.nonNull(guid) ? guid.toString() : null;
    }

    public String computeRequestHash(TemplateDataContext dataContext) {
        Object hash =
                dataContext.getAdditionalValues().get(TemplateDataContext.SP_REQUEST_HASH_KEY);
        return Objects.nonNull(hash) ? hash.toString() : null;
    }

    private String stripXmlExtension(String fileName) {
        String fileNameWithoutExt = fileName;
        String fileExtension = FilenameUtils.getExtension(fileName);
        if (fileExtension.equals(XML_EXTENSION)) {
            fileNameWithoutExt = FilenameUtils.getBaseName(fileName);
        }
        return fileNameWithoutExt;
    }

    private String computeFileExtension(FileType fileType) {
        String ext = "";
        switch (fileType) {
            case REQUEST:
            case XML:
                ext = XML_EXTENSION;
                break;
            default:
                // расширение по умолчанию -- пустая строка.
        }
        return ext;
    }
}
