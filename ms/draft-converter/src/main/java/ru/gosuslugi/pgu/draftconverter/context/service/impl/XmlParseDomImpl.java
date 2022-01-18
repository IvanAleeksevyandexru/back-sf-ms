package ru.gosuslugi.pgu.draftconverter.context.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.draftconverter.data.XmlElement;
import ru.gosuslugi.pgu.draftconverter.context.data.XmlElementImpl;
import ru.gosuslugi.pgu.draftconverter.context.service.ParseService;

/**
 * Реализует парсинг XML в DOM, используя обертки.
 *
 * @see XmlElement
 * @see ru.gosuslugi.pgu.draftconverter.data.XmlAttr
 */
@Service
@Slf4j
public class XmlParseDomImpl implements ParseService<XmlElement> {

    @Override
    public XmlElement parse(String xml) {
        Document root = unmarshall(xml);
        return new XmlElementImpl(root.getRootElement());
    }

    @SneakyThrows
    private Document unmarshall(String xml) {
        return DocumentHelper.parseText(xml);
    }
}
