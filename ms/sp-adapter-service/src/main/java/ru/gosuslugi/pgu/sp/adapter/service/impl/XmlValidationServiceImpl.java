package ru.gosuslugi.pgu.sp.adapter.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import ru.gosuslugi.pgu.sp.adapter.service.XmlValidationService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Slf4j
public class XmlValidationServiceImpl implements XmlValidationService {
    @Override
    public void validate(String xml) {
        if (StringUtils.isEmpty(xml)) {
            return;
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            log.error("There are errors in XML structure: " + xml, e);
            throw new RuntimeException(e);
        }
    }
}
