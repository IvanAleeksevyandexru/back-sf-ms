package ru.gosuslugi.pgu.xmlservice.storing.service.impl;

import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;
import ru.gosuslugi.pgu.terrabyte.client.model.FileType;
import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.data.StoreResponse;
import ru.gosuslugi.pgu.xmlservice.exception.StoreException;
import ru.gosuslugi.pgu.xmlservice.storing.service.FileNamingStrategy;
import ru.gosuslugi.pgu.xmlservice.storing.service.FileStoreService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * Сохраняет файл в Терабайт.
 */
@Service
@RequiredArgsConstructor
public class FileStoreServiceImpl implements FileStoreService {
    private final TerrabyteClient terrabyteClient;
    private final FileNamingStrategy namingStrategy;

    /**
     * Проверяет, что аргумент не null, ненулевой длины и не состоит только из нулевых элементов.
     */
    private static boolean isEmpty(byte[] arr) {
        if (arr == null || arr.length == 0) {
            return true;
        }
        for (byte b : arr) {
            if (b != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public StoreResponse store(byte[] xmlContent, TemplateDataContext templateContext) {
        if (isEmpty(xmlContent)) {
            throw new StoreException("Сохранение файла не удалось,"
                    + " т. к. он имеет пустое содержимое.");
        }
        String fileName = namingStrategy.computeFileName(templateContext);
        String mnemonic = namingStrategy.computeMnemonic(templateContext);
        terrabyteClient.internalSaveFile(xmlContent, fileName, mnemonic,
                MediaType.APPLICATION_XML_VALUE, templateContext.getOrderId(),
                FileType.ATTACHMENT.getType());
        return new StoreResponse(mnemonic);
    }
}
