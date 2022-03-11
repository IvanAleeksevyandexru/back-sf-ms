package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.SmevRequestDto;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.dto.esep.SignedFileInfo;
import ru.gosuslugi.pgu.dto.pdf.data.AttachmentType;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.dto.util.DraftUtil;
import ru.gosuslugi.pgu.sp.adapter.data.SmevRequest;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterInputDataException;
import ru.gosuslugi.pgu.sp.adapter.service.PdfPackageService;
import ru.gosuslugi.pgu.sp.adapter.service.ServiceProcessingClient;
import ru.gosuslugi.pgu.sp.adapter.service.SmevPdfService;
import ru.gosuslugi.pgu.sp.adapter.service.SmevService;
import ru.gosuslugi.pgu.sp.adapter.service.SmevXmlService;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatesDataContextService;
import ru.gosuslugi.pgu.sp.adapter.service.suggestion.SuggestionServiceNotifier;
import ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext.MAIN_ROLE_NAME;

/**
 * Template engine that inserts draft data as variables in order to transform form data into SMEV request
 * checks if template is supported and exists
 * Optional loads template (in case of storing it in casandra or other storage)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmevServiceImpl implements SmevService {

    private static final String MASTER_OID_ATTR_NAME = "masterOid";

    private static final int SECONDS_PER_DAY = 60 * 60 * 24;

    private final SuggestionServiceNotifier suggestionServiceNotifier;

    private final ServiceProcessingClient serviceProcessingClient;

    private final DraftClient draftClient;

    private final TemplatesDataContextService templatesDataContextService;

    private final SmevPdfService smevPdfService;

    private final SmevXmlService smevXmlService;

    private final PdfPackageService pdfPackageService;

    private final AttachmentService attachmentService;

    /**
     * Method for processing incomming request from delirium
     *
     * @param serviceId
     * @param requestToSend
     */
    @Override
    public boolean processSmevRequest(String serviceId, List<SpAdapterDto> requestToSend, Boolean skip17Status) {
        requestToSend.forEach(spAdapterDto -> {
            processSmevRequest(serviceId, spAdapterDto.getOrderId(), spAdapterDto.getOid(), spAdapterDto.getRole(), spAdapterDto.getOrgId(), skip17Status);
        });
        return true;
    }


    /**
     * Method for processing incomming request for sending SMEV request
     *
     * @param serviceId
     * @param orderId
     * @param oid
     * @param roleId
     */
    @Override
    public boolean processSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, Boolean skip17Status) {
        DraftHolderDto draft = draftClient.getDraftById(orderId, oid, orgId);

        if (draft == null) {
            log.error("Cannot retrieve draft with orderId {} for user with oid {}", orderId, oid);
            return false;
        }

        /*
        TODO временная мера, т.к. удаление файлов должно будет выпилено из sp-adapter-а
        после этого рефакторинга эти два метода схлопнутся в один
        */
        return (draft.getBody().getSignInfoMap().size() > 0) ?
                processSignedSmevRequest(serviceId, orderId, oid, roleId, orgId, draft, skip17Status) :
                processSmevRequest(serviceId, orderId, oid, roleId, orgId, draft, skip17Status);

    }

    @Override
    public boolean processSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, DraftHolderDto draft, Boolean skip17Status) {
        TemplatesDataContext templatesDataContext = templatesDataContextService.prepareRequestParameters(serviceId, orderId, oid, roleId, draft.getBody(), orgId, skip17Status);

        // Настройки по умолчанию, для поддержания старого кода json услуги.
        if (templatesDataContext.getFiles() == null) {
            templatesDataContext.setFiles(DefaultOptionsSpConfig.getDefaultOptions(templatesDataContext));
        }
        String smevRequest = prepareFilesAndRequestFromSpConfig(templatesDataContext, orderId, draft.getBody());

        if (smevRequest == null) {
            log.error("Cannot generate SMEV request for orderId {}", orderId);
            return false;
        }

        draft.getBody().setGeneratedFiles(templatesDataContext.getGeneratedFiles());
        draftClient.saveDraft(draft.getBody(), serviceId, oid, draft.getOrgId(), convertSecondsToDays(draft.getOrderTtlInSec()), convertSecondsToDays(draft.getOrderTtlInSec()));

        return handleSpSend(templatesDataContext, smevRequest);
    }

    @Override
    public boolean processSignedSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, Boolean skip17Status) {
        DraftHolderDto draft = draftClient.getDraftById(orderId, oid, orgId);
        return processSignedSmevRequest(serviceId, orderId, oid, roleId, orgId, draft, skip17Status);
    }

    @Override
    public boolean processSignedSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, DraftHolderDto draft, Boolean skip17Status) {
        if (draft == null) {
            log.error("Cannot retrieve draft with orderId {} for user with oid {}", orderId, oid);
            return false;
        }

        TemplatesDataContext templatesDataContext = templatesDataContextService.prepareRequestParametersWithoutRemoveOldFiles(serviceId, orderId, oid, roleId, draft.getBody(), orgId, skip17Status);

        retrieveSignedAttachmentInfo(draft.getBody(), templatesDataContext);

        String transportXml = smevXmlService.getSmevRequestOnly(templatesDataContext, DefaultOptionsSpConfig.getOptionTransportXml(templatesDataContext));
        String smevRequest =  (Objects.nonNull(transportXml) && transportXml.length() > 0) ?
                transportXml : smevXmlService.getSmevRequestOnly(templatesDataContext, DefaultOptionsSpConfig.getOptionBusinessXml(templatesDataContext));

        if (smevRequest == null) {
            log.error("Cannot generate SMEV request for orderId {}", orderId);
            return false;
        }

        return handleSpSend(templatesDataContext, smevRequest);
    }

    @Override
    public SmevRequestDto createXmlAndPdf(Long orderId, Long oid, Long orgId, String requestGuid, Boolean skip17Status) {
        DraftHolderDto draft = draftClient.getDraftById(orderId, oid, orgId);

        if (draft == null) {
            String errorInfo = "Cannot retrieve draft with orderId "+ orderId + "for user with oid "+oid;
            log.error(errorInfo);
            throw new SpAdapterInputDataException(errorInfo);
        }
        TemplatesDataContext templatesDataContext = templatesDataContextService.prepareRequestParameters(draft.getBody().getServiceCode(), orderId, oid, MAIN_ROLE_NAME, draft.getBody(), draft.getOrgId(), skip17Status);
        templatesDataContext.setRequestGuid(requestGuid);

        // Настройки по умолчанию, для поддержания старого кода json услуги.
        if(templatesDataContext.getFiles() == null) {
            templatesDataContext.setFiles(DefaultOptionsSpConfig.getDefaultOptions(templatesDataContext));
        }
        String smevRequest = prepareFilesAndRequestFromSpConfig(templatesDataContext, orderId, draft.getBody());
        if (smevRequest == null) {
            String errorInfo = "Cannot generate SMEV request for orderId "+ orderId + "for user with oid "+oid;
            log.error(errorInfo);
            throw new SpAdapterInputDataException(errorInfo);
        }

        draft.getBody().setGeneratedFiles(templatesDataContext.getGeneratedFiles());

        draftClient.saveDraft(draft.getBody(), templatesDataContext.getServiceId(), oid, draft.getOrgId(), convertSecondsToDays(draft.getTtlInSec()), convertSecondsToDays(draft.getOrderTtlInSec()));

        return new SmevRequestDto(templatesDataContext.getRequestGuid());
    }

    private String prepareFilesAndRequestFromSpConfig(TemplatesDataContext templatesDataContext, Long orderId, ScenarioDto draft) {

        // При необходимости удаляем все файлы созданные в предыдущий раз
        removeOldGeneratedAndUploadedFiles(draft.getGeneratedFiles(), orderId);

        // PDF
        templatesDataContext.getFiles().stream()
                .filter(file -> (file.getType() == FileType.COMMON_PDF || file.getType() == FileType.PDF) &&
                        (StringUtils.isEmpty(file.getAddRule()) || shouldAddFile(file.getAddRule(), draft)))
                .forEach(fileDescription -> smevPdfService.handlePdfAttachments(templatesDataContext, fileDescription));
        pdfPackageService.packageToPdf(templatesDataContext);
        // Transport XML
        List<String> listTransportXml = templatesDataContext.getFiles().stream()
                .filter(file -> (file.getType() == FileType.XML) &&
                        (StringUtils.isEmpty(file.getAddRule()) || shouldAddFile(file.getAddRule(), draft)))
                .map(fileDescription -> smevXmlService.getSmevRequest(templatesDataContext, fileDescription))
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
        boolean transportXmlEmpty = listTransportXml.isEmpty();
        // Business XML
        if (templatesDataContext.getFiles().stream()
                .filter(file -> file.getType() == FileType.REQUEST)
                .count() == 0) {
            log.error("Not found option in spConfig for request file (business xml), serviceId {}", templatesDataContext.getServiceId());
        }
        List<String> smewRequestFiles = templatesDataContext.getFiles().stream()
                .filter(file -> file.getType() == FileType.REQUEST)
                .peek(fileDescription -> {
                    // saving business file into lk only (in case transport xml is missing)
                    if (transportXmlEmpty)
                        fileDescription.setAttachmentType(AttachmentType.REQUEST);
                })
                .map(fileDescription -> smevXmlService.getSmevRequest(templatesDataContext, fileDescription))
                .collect(Collectors.toList());
        boolean requestSuccess = smewRequestFiles.stream()
                .filter(smewRequest -> smewRequest == null || smewRequest.length() == 0)
                .count() == 0;

        if (!requestSuccess) {
            log.error("No request files created, serviceId {}", templatesDataContext.getServiceId());
            return null;
        }

        if (!transportXmlEmpty) {
            return listTransportXml.get(0);
        } else
            return smewRequestFiles.get(0);
    }

    /**
     * Проверка на необходимостьь добавления файла в набор файлов для отправки
     * @param path путь до компонента, содержащего true или false в зависимости от необходимости добавления файла
     * @param scenarioDto черновик
     * @return true/false
     */
    private boolean shouldAddFile(String path, ScenarioDto scenarioDto){
        String shouldAddFile = DraftUtil.getValueByLink(scenarioDto, path);
        return Boolean.parseBoolean(shouldAddFile);
    }

    @Override
    public boolean handleSpSend(TemplatesDataContext templatesDataContext, String smevRequest) {
        Long oid = templatesDataContext.getOid();
        Long orderId = templatesDataContext.getOrderId();
        if (oid < 0) {
            if (templatesDataContext.getAdditionalValues().containsKey(MASTER_OID_ATTR_NAME)) {
                log.info("Updating oid (from fake to real master oid) to send to SP");
                oid = Long.parseLong(templatesDataContext.getAdditionalValues().get(MASTER_OID_ATTR_NAME).toString());
            } else {
                log.error("Cannot change fake oid: master oid is not provided");
                return false;
            }
        }

        if (nonNull(smevRequest) && smevRequest.length() > 0) {
            //in some cases master applicant might not send a real request
            val smevRequestPrms = SmevRequest.builder()
                    .serviceCode(templatesDataContext.getServiceId())
                    .orderId(templatesDataContext.getOrderId())
                    .oid(oid)
                    .orgId(templatesDataContext.getOrgId())
                    .orgType(templatesDataContext.getOrgType())
                    .body(smevRequest)
                    .files(templatesDataContext.getAttachments())
                    .requestGuid(templatesDataContext.getRequestGuid())
                    .systemAuthority(templatesDataContext.getSystemAuthority())
                    .additionalHttpHeader(templatesDataContext.getReplacedHeaders())
                    .serviceIdCustomName(templatesDataContext.getServiceCustomId())
                    .authorityId(templatesDataContext.getAuthorityId())
                    .skip17Status(templatesDataContext.getSkip17Status())
                    .hasEmpowerment(Objects.nonNull(templatesDataContext.getAuthorityId()))
                    .hasEmpowerment2021(templatesDataContext.getEmpowerments().stream().anyMatch(emp -> templatesDataContext.getRequiredEmpowerments().contains(emp)))
                    .reusePaymentUin(templatesDataContext.getReusePaymentUin())
                    .build();
            serviceProcessingClient.orderCall(smevRequestPrms);
            suggestionServiceNotifier.send(oid, orderId);
        }

        return true;
    }

    private void removeOldGeneratedAndUploadedFiles(Set<String> generatedFiles, Long orderId) {
        generatedFiles.forEach(fileMnemonic -> {
            log.debug("Removing old generated and uploaded files file {} from orderId {}", fileMnemonic, orderId);
            attachmentService.removeFile(orderId, fileMnemonic);
        });
        generatedFiles.clear();
    }

    private void retrieveSignedAttachmentInfo(ScenarioDto draft, TemplatesDataContext dataContext) {
        if(Objects.isNull(draft.getSignInfoMap()))
            return;

        HashSet<String> attachments = new HashSet<>();
        draft.getSignInfoMap().forEach((o, s) -> {
            attachments.addAll(s.getSignedFilesInfo().stream().map(SignedFileInfo::getMnemonic).collect(Collectors.toSet()));
        });
        dataContext.setAttachments(attachments);
    }

    private Integer convertSecondsToDays(Integer seconds) {
        if (seconds != null) {
            int days = seconds / SECONDS_PER_DAY;
            return days == 0 ? null : days;
        }
        return null;
    }
}
