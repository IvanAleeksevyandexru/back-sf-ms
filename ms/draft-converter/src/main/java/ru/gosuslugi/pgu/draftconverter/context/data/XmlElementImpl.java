package ru.gosuslugi.pgu.draftconverter.context.data;

import lombok.EqualsAndHashCode;
import org.dom4j.Attribute;
import org.dom4j.Element;
import ru.gosuslugi.pgu.draftconverter.data.XmlAttr;
import ru.gosuslugi.pgu.draftconverter.data.XmlElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Базовая реализация {@link XmlElement}.
 * <p>
 * Реализован на лениво-инициализируемых списках дочерних элементов и атрибутов.
 * <p>
 * Неизменяемый.
 */
@EqualsAndHashCode
public class XmlElementImpl implements XmlElement {
    private final Element orig;
    private List<XmlElement> content;
    private List<XmlAttr> attrs;

    public XmlElementImpl(Element original) {
        this.orig = original;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Делегирует вызов {@link Element#getQualifiedName()}.
     */
    @Override
    public String getQualifiedName() {
        return orig.getQualifiedName();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Делегирует вызов {@link Element#getName()}.
     */
    @Override
    public String getName() {
        return orig.getName();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Делегирует вызов {@link Element#getText()}.
     */
    @Override
    public String getText() {
        return orig.getText();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Если элемента с указанным именем не существует, то null.
     *
     * @return дочерний элемент с совпадающим именем или null.
     */
    @Override
    public XmlElement get(String name) {
        if (Objects.isNull(content)) {
            initContent();
        }
        return content.stream().reduce(null, (res, next) -> Objects.isNull(res)
                && Objects.equals(next.getName(), name) ? next : res);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Если элемента с указанным индексом не существует, то null.
     *
     * @return дочерний элемент по указанному индексу или null.
     */
    @Override
    public XmlElement get(int index) {
        if (Objects.isNull(content)) {
            initContent();
        }
        return index >= 0 && index < content.size() ? content.get(index) : null;
    }

    @Override
    public XmlElement getQualified(String fullyQualifiedName) {
        if (Objects.isNull(content)) {
            initContent();
        }
        return content.stream().reduce(null, (res, next) -> Objects.isNull(res)
                && Objects.equals(next.getQualifiedName(), fullyQualifiedName) ? next : res);
    }

    @Override
    public List<XmlElement> children() {
        if (Objects.isNull(content)) {
            initContent();
        }
        return new ArrayList<>(content);
    }

    @Override
    public List<XmlElement> children(String name) {
        if (Objects.isNull(content)) {
            initContent();
        }
        return content.stream().filter(el -> Objects.equals(el.getName(), name)).collect(Collectors.toList());
    }

    @Override
    public List<XmlElement> childrenQualified(String fullyQualifiedName) {
        if (Objects.isNull(content)) {
            initContent();
        }
        return content.stream().filter(el -> Objects.equals(el.getQualifiedName(), fullyQualifiedName)).collect(Collectors.toList());
    }

    @Override
    public int childrenCount() {
        return orig.elements().size();
    }

    /**
     * Возвращает копию списка с атрибутами элемента.
     */
    @Override
    public List<XmlAttr> attrs() {
        if (Objects.isNull(attrs)) {
            initAttrs();
        }
        return new ArrayList<>(attrs);
    }

    @Override
    public int attrsCount() {
        return orig.attributeCount();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Если указанного атрибута не найдено, возвращается null.
     */
    @Override
    public XmlAttr attr(int index) {
        if (Objects.isNull(attrs)) {
            initAttrs();
        }
        return index >= 0 && index < attrs.size() ? attrs.get(index) : null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Если указанного атрибута не найдено, возвращается null.
     */
    @Override
    public XmlAttr attr(String name) {
        if (Objects.isNull(attrs)) {
            initAttrs();
        }
        return attrs.stream().reduce(null, (res, next) -> Objects.isNull(res)
                && Objects.equals(next.getName(), name) ? next : res);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Если указанного атрибута не найдено, возвращается null.
     */
    @Override
    public XmlAttr attrQualified(String fullyQualifiedName) {
        if (Objects.isNull(attrs)) {
            initAttrs();
        }
        return attrs.stream().reduce(null, (res, next) -> Objects.isNull(res)
                && Objects.equals(next.getQualifiedName(), fullyQualifiedName) ? next : res);
    }

    private void initContent() {
        content = new ArrayList<>();
        for (Object element : (List<?>) orig.elements()) {
            content.add(new XmlElementImpl((Element) element));
        }
    }

    private void initAttrs() {
        attrs = new ArrayList<>();
        for (Object attr : orig.attributes()) {
            attrs.add(new XmlAttrImpl((Attribute) attr));
        }
    }

    /**
     * Делегирует вызов {@link #getText()}.
     *
     * @return текстовое значение элемента.
     */
    @Override
    public String toString() {
        return getText();
    }
}
