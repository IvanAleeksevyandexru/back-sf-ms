package ru.gosuslugi.pgu.sp.adapter.service;

import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.SmevRequestDto;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

import java.util.List;

/**
 * Service responsible for all sending logic
 * checking messages (removing reduntant files etc)
 * used in SmevController
 */
public interface SmevService {
    boolean processSmevRequest(String serviceId, List<SpAdapterDto> requestToSend, Boolean skip17Status);

    boolean processSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, Boolean skip17Status);

    boolean processSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, DraftHolderDto draft, Boolean skip17Status);

    boolean processSignedSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, Boolean skip17Status);

    boolean processSignedSmevRequest(String serviceId, Long orderId, Long oid, String roleId, Long orgId, DraftHolderDto draft, Boolean skip17Status);

    SmevRequestDto createXmlAndPdf(Long orderId, Long oid, Long orgId, String requestGuid, Boolean skip17Status);

    boolean handleSpSend(TemplatesDataContext templatesDataContext, String smevRequest);

}
