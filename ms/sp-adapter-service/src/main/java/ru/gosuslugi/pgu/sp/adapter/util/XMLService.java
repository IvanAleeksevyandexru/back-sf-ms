package ru.gosuslugi.pgu.sp.adapter.util;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Optional;

public class XMLService {

    public String unescape(Object value) {
        return Optional.ofNullable(value)
            .map(Object::toString)
            .map(StringEscapeUtils::unescapeXml)
            .orElse(null);
    }
}
