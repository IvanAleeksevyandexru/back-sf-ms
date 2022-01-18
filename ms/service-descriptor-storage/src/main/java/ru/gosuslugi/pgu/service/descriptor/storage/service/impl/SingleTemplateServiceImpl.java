package ru.gosuslugi.pgu.service.descriptor.storage.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.service.descriptor.storage.service.SingleTemplateService;
import ru.gosuslugi.pgu.service.descriptor.storage.service.TemplatePackageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class SingleTemplateServiceImpl implements SingleTemplateService {

    @Autowired
    TemplatePackageService templatePackageService;

    @Override
    public ByteBuffer get(String serviceId, String path) throws IOException {

        ByteBuffer zipByteBuffer = templatePackageService.get(serviceId).getPackageFile();
        byte[] byteArray = new byte[zipByteBuffer.remaining()];
        zipByteBuffer.get(byteArray);

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(byteArray))) {
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                if (entry.getName().equals(path)) {
                    return ByteBuffer.wrap(zis.readAllBytes());
                }
            }
        }
        return null;
    }

    @Override
    public Long getCRC(String serviceId, String path) throws IOException {
        ByteBuffer byteBuffer = get(serviceId, path);
        if(byteBuffer == null){
            return null;
        }
        CRC32 crc32 = new CRC32();
        crc32.update(byteBuffer);
        return crc32.getValue();
    }


}
