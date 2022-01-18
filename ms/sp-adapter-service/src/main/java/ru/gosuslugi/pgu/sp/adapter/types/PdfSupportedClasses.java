package ru.gosuslugi.pgu.sp.adapter.types;

/**
 * List of supported and used element's class for pdf generation
 * Scope of supported classes can be found in pdf.xslt ($className)
 * Example of common service config:
 * - Получение справки о наличии... (Form)
 * - 1. Сведения о заявителе (FormStep)
 * - Фамилия Иванов (FieldText)
 * - Имя Иван (FieldText)
 * - 2. Адрес проверяемого лица (FormStep)
 * - Адрес регистрации (Panel)
 * - Индекс 1111111 (FieldText)
 * - Город Москва (FieldText)
 * - Фактический адрес проживания лица (Panel)
 * - Индекс 1111111 (FieldText)
 * - Город Москва (FieldText)
 */
public enum PdfSupportedClasses {
    /**
     * Used for title section in pdf file (usually contains name of a service)
     */
    Form,

    /**
     * Used for step section. Should have numbered value
     * Example:
     * 1. Сведения о заявителе
     * 2. Контактные сведения
     */
    FormStep,

    /**
     * Used in case if there are several inner section in FormStep
     * Example:
     * 1. Адрес проверяемого лица (FormStep)
     * Адрес регистрации (Panel)
     * Фактический адрес проживания лица (Panel)
     */
    Panel,

    /**
     * Used for values elements
     */
    FieldText

}
