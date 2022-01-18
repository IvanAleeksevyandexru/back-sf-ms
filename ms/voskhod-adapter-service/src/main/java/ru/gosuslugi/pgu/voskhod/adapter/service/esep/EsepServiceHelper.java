package ru.gosuslugi.pgu.voskhod.adapter.service.esep;

import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CreateUIToSingResponse;
import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.FileCertificateUserInfo;
import ru.nvg.idecs.storageservice.ws.types.EsepFile;

import java.util.List;

public interface EsepServiceHelper {
    CreateUIToSingResponse createUIToSingEx(String returnUrl, List<EsepFile> fileAccessCodes);

    List<FileCertificateUserInfo> getFileCertificateUserInfos(List<String> fileAccessCodes);
}
