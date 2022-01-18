package ru.gosuslugi.pgu.sp.adapter.service.input;


import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import ru.gosuslugi.pgu.sp.adapter.types.EscaperType;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * https://stackoverflow.com/questions/439298/best-way-to-encode-text-data-for-xml-in-java
 */
public class XmlEscapingTest {

    @Test
    public void test() throws IOException {

        assertEquals("Hello &quot;World&quot;", StringEscapeUtils.escapeXml11("Hello \"World\""));
        assertEquals("&lt;Саха&gt; /&lt;Якутия&gt;/", StringEscapeUtils.escapeXml11("<Саха> /<Якутия>/"));
    }

    @Test
    public void testEscaper() throws IOException {

        assertEquals("Hello &quot;World&quot;", EscaperType.XML.getEscaper().apply("Hello \"World\""));
        assertEquals("&lt;Саха&gt; /&lt;Якутия&gt;/", EscaperType.XML.getEscaper().apply("<Саха> /<Якутия>/"));
    }
}
