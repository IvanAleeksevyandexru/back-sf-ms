package ru.gosuslugi.pgu.draftconverter.context.data;

import lombok.EqualsAndHashCode;
import org.dom4j.Attribute;
import ru.gosuslugi.pgu.draftconverter.data.XmlAttr;

@EqualsAndHashCode
public class XmlAttrImpl implements XmlAttr {
    private final Attribute orig;

    public XmlAttrImpl(Attribute original) {
        orig = original;
    }

    @Override
    public String getName() {
        return orig.getName();
    }

    @Override
    public String getQualifiedName() {
        return orig.getQualifiedName();
    }

    @Override
    public String getValue() {
        return orig.getValue();
    }

    /**
     * Делегирует вызов {@link #getValue()}.
     *
     * @return значение атрибута.
     */
    @Override
    public String toString() {
        return getValue();
    }
}
