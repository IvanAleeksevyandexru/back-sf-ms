package ru.gosuslugi.pgu.voskhod.adapter.mapper;

import org.datacontract.schemas._2004._07.granit_esep_ppnp_webservices_contracts.CreateUIToSingResponse;
import org.mapstruct.Mapper;
import ru.gosuslugi.pgu.dto.esep.PrepareSignResponse;

@Mapper
public interface EsepMapper {

    PrepareSignResponse toPrepareSignResponse(CreateUIToSingResponse signResponse);
}
