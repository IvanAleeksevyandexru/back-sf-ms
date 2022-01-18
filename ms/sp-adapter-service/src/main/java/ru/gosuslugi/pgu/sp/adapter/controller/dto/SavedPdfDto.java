package ru.gosuslugi.pgu.sp.adapter.controller.dto;

import lombok.*;

/**
 * Класс описания ответа от terrabyte
 */
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class SavedPdfDto {
    /** мнемоника сохраненного файла */
    private final String mnemonic;
}
