package ru.gosuslugi.pgu.xmlservice.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(onConstructor_ = {@JsonCreator})
@Schema(description = "Ответ сервера на запрос генерация и сохранения файла")
public class StoreResponse {
    /**
     * Присвоенная файлу мнемоника.
     */
    @Schema(description = "Мнемоника, присвоенная файлу при сохранении")
    private final String mnemonic;
}
