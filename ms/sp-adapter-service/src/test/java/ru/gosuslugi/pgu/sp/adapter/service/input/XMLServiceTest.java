package ru.gosuslugi.pgu.sp.adapter.service.input;


import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import ru.gosuslugi.pgu.sp.adapter.types.EscaperType;
import ru.gosuslugi.pgu.sp.adapter.util.XMLService;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * https://stackoverflow.com/questions/439298/best-way-to-encode-text-data-for-xml-in-java
 */
public class XMLServiceTest {

    @Test
    public void unescape() throws IOException {
        assertEquals("Hello \"World\"", new XMLService().unescape("Hello &quot;World&quot;"));
        assertEquals("<Саха> /<Якутия>/", new XMLService().unescape("&lt;Саха&gt; /&lt;Якутия&gt;/"));
    }
}
