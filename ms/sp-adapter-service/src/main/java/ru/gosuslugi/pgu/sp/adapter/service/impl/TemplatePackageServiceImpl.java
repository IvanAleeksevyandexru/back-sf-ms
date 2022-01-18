package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient;
import ru.gosuslugi.pgu.sp.adapter.config.props.VelocityProperties;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterConfigurationException;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatePackageService;
import ru.gosuslugi.pgu.sp.adapter.types.PackageProcessingStatus;
import ru.gosuslugi.pgu.sp.adapter.types.ProcessingStatus;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "service-descriptor-storage-client.integration", havingValue = "true", matchIfMissing = true)
public class TemplatePackageServiceImpl implements TemplatePackageService {

    private static final String TEMP_FILE_PREFIX = "vm-templates";
    private static final String TEMP_FILE_SUFFIX = ".zip";
    public static final String CHECKSUM_FILE_SUFFIX = ".checksum";
    public static final String EMPTY_CHECKSUM = "";

    private final Map<String, ReentrantReadWriteLock> serviceTemplateLocks = new ConcurrentHashMap<>();

    private final Map<String, PackageProcessingStatus> packageProcessingStatusMap;

    private final VelocityProperties velocityProperties;

    private final ServiceDescriptorClient serviceDescriptorClient;

    public void loadTemplatePackage(String serviceId) {

        ByteBuffer templatesPack = null;
        try {
            templatesPack = serviceDescriptorClient.getTemplatePackage(serviceId);
        } catch (Exception e) {
            throw new SpAdapterConfigurationException(String.format("Template package error: %s", e.getMessage()));
        }

        if (!serviceTemplateLocks.containsKey(serviceId))
            serviceTemplateLocks.putIfAbsent(serviceId, new ReentrantReadWriteLock());
        try {
            unPack(templatesPack, serviceId);
            setPackageProcessingStatus(serviceId, ProcessingStatus.REFRESH_SUCCESS, "templates loaded");
        } catch (IOException ex) {
            setPackageProcessingStatus(serviceId, ProcessingStatus.REFRESH_FAIL, ex.getMessage());
            log.error("unpack template file exception for serviceId: {} {}", serviceId, ex.getMessage());
        }
    }

    @Override
    public ReentrantReadWriteLock getLockForService(String serviceId) {
        if (!serviceTemplateLocks.containsKey(serviceId)) {
            loadTemplatePackage(serviceId);
        }
        return serviceTemplateLocks.get(serviceId);
    }

    @Scheduled(fixedRateString = "${velocity.template-package.refresh-rate}", initialDelayString = "#{ T(java.util.concurrent.ThreadLocalRandom).current().nextInt(100*60*10) }")
    public void refreshTemplatePackages() {
        Collection<String> serviceIds = serviceTemplateLocks.keySet();
        if (serviceIds.isEmpty()) {
            log.info("Empty templates registry");
            return;
        }
        Map<String, String> mapChecksums = serviceDescriptorClient.getTemplatePackageChecksums(serviceIds);
        for (Map.Entry<String, String> entry : mapChecksums.entrySet()) {
            String serviceId = entry.getKey();
            try {
                String serviceChecksum = entry.getValue();
                String serviceDir = velocityProperties.getFileResourceLoaderPath() + File.separator + serviceId;
                String checksum = EMPTY_CHECKSUM;

                File checksumFile = new File(serviceDir, serviceId + CHECKSUM_FILE_SUFFIX);
                if (checksumFile.exists()) checksum = Files.readString(checksumFile.toPath());
                if (!checksum.equals(serviceChecksum)) loadTemplatePackage(serviceId);
                else setPackageProcessingStatus(serviceId, ProcessingStatus.ACTUAL, "templates-actual with checksum: " + checksum);
            } catch (IOException ex) {
                setPackageProcessingStatus(serviceId, ProcessingStatus.REFRESH_FAIL, ex.getMessage());
                log.error("Scheduler unpack template file exception for serviceId: {} {}", serviceId, ex.getMessage());
            }
        }
    }

    private void setPackageProcessingStatus(String serviceId, ProcessingStatus status, String statusDescription) {
        PackageProcessingStatus processingStatus = PackageProcessingStatus.builder()
                .serviceId(serviceId)
                .processedOn(LocalDateTime.now().toString())
                .status(status)
                .statusDescription(statusDescription)
                .build();
        packageProcessingStatusMap.put(serviceId, processingStatus);
    }

    private void unPack(ByteBuffer templatePackage, String serviceId) throws IOException {
        ReentrantReadWriteLock writeLock = serviceTemplateLocks.get(serviceId);
        writeLock.writeLock().lock();
        try {
            File fileZip = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            FileOutputStream zipOutputStream = new FileOutputStream(fileZip);
            FileChannel fc = zipOutputStream.getChannel();
            fc.write(templatePackage);
            fc.close();
            zipOutputStream.close();

            processZipFile(serviceId, fileZip);
            computeAndSaveZipChecksum(serviceId, templatePackage);
        } catch (IOException | NoSuchAlgorithmException | IllegalArgumentException ex) {
            log.error("Unpack template file exception for serviceId: {} {}", serviceId, ex.getMessage());
            throw new IOException(ex.getMessage(), ex.getCause());
        } finally {
            writeLock.writeLock().unlock();
        }
    }

    private void processZipFile(String serviceId, File fileZip) throws IOException {
        File destDir = new File(velocityProperties.getFileResourceLoaderPath(), serviceId);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if(zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    log.error("Failed to create templates directory for serviceId: {}", serviceId);
                    throw new IOException("Failed to create directory " + newFile);
                }
                zipEntry = zis.getNextEntry();
                continue;
            }
            // fix for Windows-created archives
            File parent = newFile.getParentFile();
            if (!parent.isDirectory() && !parent.mkdirs()) {
                log.error("Unpack template file exception for serviceId: {}", serviceId);
                throw new IOException("Failed to create directory " + parent);
            }
            // write file content
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        Files.delete(fileZip.toPath());
    }

    private void computeAndSaveZipChecksum(String serviceId, ByteBuffer templatePackage) throws NoSuchAlgorithmException, IOException {
        File destDir = new File(velocityProperties.getFileResourceLoaderPath(), serviceId);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(templatePackage.array());
        byte[] digest = md.digest();
        FileWriter fileWriter = new FileWriter(destDir.getAbsolutePath() + File.separator + serviceId + CHECKSUM_FILE_SUFFIX);
        String checksum = DatatypeConverter.printHexBinary(digest).toUpperCase();
        fileWriter.write(checksum);
        fileWriter.close();
    }

    private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            log.error("Zip entry is outside of the target dir: {}", destinationDir.getAbsolutePath());
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}
