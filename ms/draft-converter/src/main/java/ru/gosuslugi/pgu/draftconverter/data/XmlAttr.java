package ru.gosuslugi.pgu.draftconverter.data;

/**
 * Атрибут XML-элемента.
 */
public interface XmlAttr {
    /**
     * Возвращает короткое имя без namespace.
     *
     * @return короткое имя без namespace.
     */
    String getName();

    /**
     * Возвращает полное имя с namespace.
     *
     * @return полное имя с namespace.
     */
    String getQualifiedName();

    /**
     * Возвращает значение.
     *
     * @return значение.
     */
    String getValue();
}
