package ru.gosuslugi.pgu.identification.luna;

import ru.gosuslugi.pgu.identification.core.api.dto.SelfieRequest;
import ru.gosuslugi.pgu.identification.core.api.dto.VideoRequest;
import ru.gosuslugi.pgu.identification.luna.dto.FaceIdResponse;
import ru.gosuslugi.pgu.identification.luna.dto.MatchResponse;
import ru.gosuslugi.pgu.identification.luna.dto.MatchVideoResponse;
import ru.gosuslugi.pgu.terrabyte.client.model.FileInfo;

/** Интеграция с Luna Platform */
public interface LunaService {

    /** Создание биометрического шаблона из терабайта */
    FaceIdResponse createFaceId(FileInfo passportInfo);

    /** Создание биометрического шаблона */
    FaceIdResponse createFaceId(byte[] passportInfo);

    /** Сравнение биометрических шаблонов */
    MatchResponse matchFaces(SelfieRequest selfieRequest);

    /** Сравнение кадра из видео с шаблонами из паспорта и селфи */
    MatchVideoResponse matchVideo(VideoRequest videoRequest);

}
