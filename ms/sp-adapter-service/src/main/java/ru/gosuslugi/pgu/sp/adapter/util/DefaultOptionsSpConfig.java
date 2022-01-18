package ru.gosuslugi.pgu.sp.adapter.util;

import lombok.Data;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.pdf.data.AttachmentType;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.dto.pdf.data.UniqueType;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultOptionsSpConfig {

    private static final String PATH_SEPARATOR = "/";
    private static final String FILENAME_DELIMITER = "_";

    private static final String TRANSPORT_BUSINESS_XML_ATTACHMENT_NAME = "trans";
    private static final String TRANSPORT_TEMPLATE_PREFIX = "t";
    private static final String BASE_BUSINESS_XML_ATTACHMENT_NAME = "req";
    private static final String BASE_BUSINESS_XML_ATTACHMENT_EXTENSION = ".xml";

    public static final String PDF_ATTACHMENT_NAME = "req_preview.pdf";
    public static final String PDF_ADDITIONAL_ATTACHMENT_NAME = "additional.pdf";
    public static final String PDF_ATTACHMENT_TEMPLATE_PREFIX = "pdf";
    public static final String ADDITIONAL_APPLICATION_TEMPLATE_PREFIX = "pdf_add";
    public static final String APPROVAL_APPLICATION_TEMPLATE_PREFIX = "pdf_apr";

    @Data
    public static class FileNameAndList {
        String fileName;
        List<ApplicantRole> roleList;
    }

    public static List<FileDescription> getDefaultOptions(TemplatesDataContext dataContext) {
        List<FileDescription> list = new ArrayList<>(){{
                add(getOptionBusinessXml(dataContext));
                add(getOptionTransportXml(dataContext));
                add(getOptionCommonPdf(dataContext));
                add(getOptionAdditionalPdf(dataContext));
            }};
        list.addAll(getPdfAdditionalFilesFromConfigList(dataContext));
        return list;
    }

    public static FileDescription getOptionBusinessXml(TemplatesDataContext dataContext) {
        FileDescription request = new FileDescription();
        request.setType(FileType.REQUEST);
        String requestAttachmentFilename = dataContext.getBusinessXmlName() == null
                ? BASE_BUSINESS_XML_ATTACHMENT_NAME + BASE_BUSINESS_XML_ATTACHMENT_EXTENSION
                : dataContext.getBusinessXmlName();
        request.setFileName(requestAttachmentFilename);
        UniqueType uniqueType = dataContext.getBusinessXmlName() == null ? UniqueType.GUID : UniqueType.NONE;
        request.setAddedFileName(uniqueType);
        request.setAttachmentType(AttachmentType.LK);
        Map<ApplicantRole, String> templatesRequest = Map.of(
                ApplicantRole.Applicant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.Applicant, null),
                ApplicantRole.Coapplicant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.Coapplicant, null),
                ApplicantRole.ChildrenUnder14, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.ChildrenUnder14, null),
                ApplicantRole.ChildrenAbove14, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.ChildrenAbove14, null),
                ApplicantRole.NotParticipant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.NotParticipant, null)
        );
        request.setTemplates(templatesRequest);
        return request;
    }

    public static FileDescription getOptionTransportXml(TemplatesDataContext dataContext) {
        FileDescription transport = new FileDescription();
        transport.setType(FileType.XML);
        String transportAttachmentFilename = TRANSPORT_BUSINESS_XML_ATTACHMENT_NAME + BASE_BUSINESS_XML_ATTACHMENT_EXTENSION;
        transport.setFileName(transportAttachmentFilename);
        transport.setAddedFileName(UniqueType.GUID);
        transport.setAttachmentType(AttachmentType.REQUEST);
        Map<ApplicantRole, String> templatesTransport = Map.of(
                ApplicantRole.Applicant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), null, TRANSPORT_TEMPLATE_PREFIX),
                ApplicantRole.Coapplicant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), null, TRANSPORT_TEMPLATE_PREFIX),
                ApplicantRole.ChildrenUnder14, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), null, TRANSPORT_TEMPLATE_PREFIX),
                ApplicantRole.ChildrenAbove14, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), null, TRANSPORT_TEMPLATE_PREFIX),
                ApplicantRole.NotParticipant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), null, TRANSPORT_TEMPLATE_PREFIX)
        );
        transport.setTemplates(templatesTransport);
        return transport;
    }

    public static FileDescription getOptionCommonPdf(TemplatesDataContext dataContext) {
        FileDescription pdfFile = new FileDescription();
        pdfFile.setType(FileType.COMMON_PDF);
        String pdfAttachmentFilename = PDF_ATTACHMENT_NAME;
        pdfFile.setFileName(pdfAttachmentFilename);
        pdfFile.setAddedFileName(UniqueType.NONE);
        pdfFile.setMnemonic(PDF_ATTACHMENT_NAME);
        pdfFile.setAddedMnemonic(UniqueType.NONE);
        pdfFile.setAttachmentType(dataContext.isRegPreviewSendToSP() ? AttachmentType.LK : AttachmentType.REQUEST);
        Map<ApplicantRole, String> templatesPdf = Map.of(
                ApplicantRole.Applicant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.Applicant, PDF_ATTACHMENT_TEMPLATE_PREFIX),
                ApplicantRole.Coapplicant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.Coapplicant, PDF_ATTACHMENT_TEMPLATE_PREFIX),
                ApplicantRole.ChildrenUnder14, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.ChildrenUnder14, PDF_ATTACHMENT_TEMPLATE_PREFIX),
                ApplicantRole.ChildrenAbove14, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.ChildrenAbove14, PDF_ATTACHMENT_TEMPLATE_PREFIX),
                ApplicantRole.NotParticipant, determineTemplateFileName(dataContext.getServiceId(), dataContext.getServiceId(), ApplicantRole.NotParticipant, PDF_ATTACHMENT_TEMPLATE_PREFIX)
        );
        pdfFile.setTemplates(templatesPdf);
        return pdfFile;
    }

    public static FileDescription getOptionAdditionalPdf(TemplatesDataContext dataContext) {
        FileDescription pdfAdditionalFile = new FileDescription();
        pdfAdditionalFile.setType(FileType.PDF);
        String pdfAdditionalAttachmentFilename = PDF_ADDITIONAL_ATTACHMENT_NAME;
        pdfAdditionalFile.setFileName(pdfAdditionalAttachmentFilename);
        pdfAdditionalFile.setAddedFileName(UniqueType.NONE);
        pdfAdditionalFile.setMnemonic(PDF_ADDITIONAL_ATTACHMENT_NAME);
        pdfAdditionalFile.setAddedMnemonic(UniqueType.NONE);
        pdfAdditionalFile.setAttachmentType(AttachmentType.LK);
        String folderAdditionalPdf = dataContext.getServiceId() + PATH_SEPARATOR + "additional";
        Map<ApplicantRole, String> templatesPdfAdditional = Map.of(
                ApplicantRole.Applicant, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), ApplicantRole.Applicant, ADDITIONAL_APPLICATION_TEMPLATE_PREFIX),
                ApplicantRole.Coapplicant, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), ApplicantRole.Coapplicant, ADDITIONAL_APPLICATION_TEMPLATE_PREFIX),
                ApplicantRole.ChildrenUnder14, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), ApplicantRole.ChildrenUnder14, ADDITIONAL_APPLICATION_TEMPLATE_PREFIX),
                ApplicantRole.ChildrenAbove14, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), ApplicantRole.ChildrenAbove14, ADDITIONAL_APPLICATION_TEMPLATE_PREFIX),
                ApplicantRole.NotParticipant, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), ApplicantRole.Approval, ADDITIONAL_APPLICATION_TEMPLATE_PREFIX)
        );
        pdfAdditionalFile.setTemplates(templatesPdfAdditional);
        updateAdditionalPdfDescriptor(pdfAdditionalFile, dataContext);
        return pdfAdditionalFile;
    }

    public static List<FileDescription> getPdfAdditionalFilesFromConfigList(TemplatesDataContext dataContext) {

        List<FileDescription> fdList = new LinkedList<>();
        Map<String, Map<String, String>> mapOfAdditionalPdfs =  dataContext.getAdditionalPdfs();

        if(mapOfAdditionalPdfs == null){
            return fdList;
        }

        Map<String, FileNameAndList> restructuredMap = new LinkedHashMap<>();
        //Переформировываем мапу для дальнейшего добавления дополнительных файлов
        //Получаем все дополнительные файлы для каждой роли
        for(String roleString : mapOfAdditionalPdfs.keySet()){
            //filesMap содержит дополнительные файлы для заданной роли
            Map<String, String> filesMap = mapOfAdditionalPdfs.get(roleString);
            if(filesMap != null){
                //Проходим по всем префиксам и достаём имена файлов
                for(String prefix : filesMap.keySet()){
                    //Если префикс является префиксом обычного доп файла то пропускаем его
                    if(ADDITIONAL_APPLICATION_TEMPLATE_PREFIX.equals(prefix)){
                        continue;
                    }
                    //Проверяем, добавляли ли мы файл с указанным префиксом для другой роли
                    FileNameAndList fileInfo = restructuredMap.get(prefix);
                    if(fileInfo == null) {
                        //Не добавляли и нужно создать новый список ролей для файла
                        fileInfo = new FileNameAndList();
                        fileInfo.setFileName(filesMap.get(prefix));
                        fileInfo.setRoleList(List.of(ApplicantRole.valueOf(roleString)));
                        restructuredMap.put(prefix, fileInfo);
                    } else {
                        //Добавляли и нужно лишь добавить роль в список ролей файла
                        fileInfo.getRoleList().add(ApplicantRole.valueOf(roleString));
                    }
                }
            }
        }

        for(String prefix : restructuredMap.keySet()) {
            String fileName = restructuredMap.get(prefix).getFileName();
            List<ApplicantRole> roleList = restructuredMap.get(prefix).getRoleList();

            FileDescription pdfAdditionalFile = new FileDescription();
            pdfAdditionalFile.setType(FileType.PDF);
            String pdfAdditionalAttachmentFilename = fileName;
            pdfAdditionalFile.setFileName(pdfAdditionalAttachmentFilename);
            pdfAdditionalFile.setAddedFileName(UniqueType.NONE);
            pdfAdditionalFile.setMnemonic(fileName);
            pdfAdditionalFile.setAddedMnemonic(UniqueType.NONE);
            pdfAdditionalFile.setAttachmentType(AttachmentType.REQUEST);
            String folderAdditionalPdf = dataContext.getServiceId() + PATH_SEPARATOR + "additional";

            Map<ApplicantRole, String> templatesPdfAdditional = new LinkedHashMap<>();
            for(ApplicantRole role : roleList){
                templatesPdfAdditional.put(role, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), role, prefix));
            }
            pdfAdditionalFile.setTemplates(templatesPdfAdditional);
            fdList.add(pdfAdditionalFile);
        }

        return fdList;
    }

    private static void updateAdditionalPdfDescriptor(FileDescription description, TemplatesDataContext dataContext) {

        Map<String, Map<String, String>> mapOfAdditionalPdfs =  dataContext.getAdditionalPdfs();

        if(mapOfAdditionalPdfs != null) {
            mapOfAdditionalPdfs.forEach((role, files) -> {
                if (files.containsKey(ADDITIONAL_APPLICATION_TEMPLATE_PREFIX)) {
                    String fileName = files.get(ADDITIONAL_APPLICATION_TEMPLATE_PREFIX);
                    description.setFileName(fileName);
                    description.setMnemonic(fileName);
                }
            });
        }
    }

    public static FileDescription getOptionApprovalPdf(TemplatesDataContext dataContext) {
        FileDescription pdfApprovalFile = new FileDescription();
        pdfApprovalFile.setType(FileType.PDF);
        String pdfApprovalAttachmentFilename = PDF_ADDITIONAL_ATTACHMENT_NAME;
        pdfApprovalFile.setFileName(pdfApprovalAttachmentFilename);
        pdfApprovalFile.setAddedFileName(UniqueType.NONE);
        pdfApprovalFile.setMnemonic(PDF_ADDITIONAL_ATTACHMENT_NAME);
        pdfApprovalFile.setAddedMnemonic(UniqueType.NONE);
        pdfApprovalFile.setAttachmentType(AttachmentType.REQUEST);
        String folderAdditionalPdf = dataContext.getServiceId() + PATH_SEPARATOR + "additional";
        Map<ApplicantRole, String> templatesPdfApproval = Map.of(
                ApplicantRole.Applicant, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), ApplicantRole.Applicant, APPROVAL_APPLICATION_TEMPLATE_PREFIX),
                ApplicantRole.Coapplicant, determineTemplateFileName(folderAdditionalPdf, dataContext.getServiceId(), ApplicantRole.Coapplicant, APPROVAL_APPLICATION_TEMPLATE_PREFIX)
        );
        pdfApprovalFile.setTemplates(templatesPdfApproval);
        return pdfApprovalFile;
    }

    public static String determineTemplateFileName(String baseFolder, String serviceId, ApplicantRole role, String prefix) {
        return baseFolder
                + PATH_SEPARATOR
                + (((prefix == null) ? "" : (prefix + FILENAME_DELIMITER))
                + serviceId
                + ((role == null) ? "" : FILENAME_DELIMITER + role));
    }
}
