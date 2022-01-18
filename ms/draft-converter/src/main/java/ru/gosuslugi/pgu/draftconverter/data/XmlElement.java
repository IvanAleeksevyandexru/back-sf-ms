package ru.gosuslugi.pgu.draftconverter.data;

import java.util.List;

/**
 * XML-элемент, упрощающий {@link org.dom4j.Element}.
 */
public interface XmlElement {
    /**
     * Возвращает полное имя вместе с namespase.
     *
     * @return полное имя вместе с namespase.
     */
    String getQualifiedName();

    /**
     * Возвращает короткое имя без namespase.
     *
     * @return короткое имя без namespase.
     */
    String getName();

    /**
     * Возвращает текстовое содержимое.
     *
     * @return текстовое содержимое.
     */
    String getText();

    /**
     * Возвращает элемент по его короткому имени.
     *
     * @param name короткое имя без namespase.
     * @return дочерний элемент с совпадающим именем.
     */
    XmlElement get(String name);

    /**
     * Возвращает элемент по его индексу.
     *
     * @param index индекс элемента.
     * @return дочерний элемент по указанному индексу.
     */
    XmlElement get(int index);

    /**
     * Возвращает элемент по его полному имени с namespace.
     *
     * @param fullyQualifiedName полное имя с namespase.
     * @return первый дочерний элемент с совпадающим полным именем.
     */
    XmlElement getQualified(String fullyQualifiedName);

    /**
     * Возвращает список дочерних элементов.
     *
     * @return копия списка дочерних элементов.
     */
    List<XmlElement> children();

    /**
     * Возвращает список дочерних элементов, короткое имя (без namespace) которых совпадает с {@code name}.
     *
     * @param name короткое имя (без namespace).
     * @return список дочерних элементов.
     */
    List<XmlElement> children(String name);

    /**
     * Возвращает список дочерних элементов, полное имя (включая namespace) которых совпадает с {@code fullyQualifiedName}.
     *
     * @param fullyQualifiedName полное имя, включая namespace.
     * @return список дочерних элементов.
     */
    List<XmlElement> childrenQualified(String fullyQualifiedName);

    /**
     * Возвращает количество дочерних элементов.
     *
     * @return количество дочерних элементов.
     */
    int childrenCount();

    /**
     * Возвращает атрибуты элемента.
     *
     * @return атрибуты элемента.
     */
    List<XmlAttr> attrs();

    /**
     * Возвращает атрибут элемента по указанному индексу index.
     *
     * @param index индекс атрибута.
     * @return атрибут элемента.
     */
    XmlAttr attr(int index);

    /**
     * Возвращает количество атрибутов.
     *
     * @return количество атрибутов.
     */
    int attrsCount();

    /**
     * Возвращает атрибут с указанным коротким именем без namespace.
     *
     * @param name короткое имя атрибута.
     * @return атрибут.
     */
    XmlAttr attr(String name);

    /**
     * Возвращает атрибут с указанным полным именем с namespace.
     *
     * @param fullyQualifiedName полное имя с namespace.
     * @return атрибут.
     */
    XmlAttr attrQualified(String fullyQualifiedName);
}
