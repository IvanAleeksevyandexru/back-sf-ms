package ru.gosuslugi.pgu.identification.core.service;

import ru.gosuslugi.pgu.identification.core.api.dto.PassportRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.PassportResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.SelfieResponse;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoResponse;

public interface IdentificationService {

    PassportResponse passportIdentification(PassportRequest passportRequest);

    SelfieResponse selfieIdentification(SelfieRequest selfieRequest);

    VideoResponse videoIdentification(VideoRequest videoRequest);

}
