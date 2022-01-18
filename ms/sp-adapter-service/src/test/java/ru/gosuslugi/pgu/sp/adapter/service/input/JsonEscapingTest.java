package ru.gosuslugi.pgu.sp.adapter.service.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONValue;
import org.junit.Test;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.sp.adapter.types.EscaperType;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * https://stackoverflow.com/questions/18898773/java-escape-json-string
 */
public class JsonEscapingTest {

    private static final ObjectMapper objectMapper = JsonProcessingUtil.getObjectMapper();

    @Test
    public void test() throws IOException {

        assertEquals("Hello \\\"World\\\"", JSONValue.escape("Hello \"World\""));
        assertEquals("Саха \\/Якутия\\/", JSONValue.escape("Саха /Якутия/"));
    }

    @Test
    public void testEscaper() throws IOException {

        assertEquals("Hello \\\"World\\\"",  EscaperType.PDF.getEscaper().apply("Hello \"World\""));
        assertEquals("Саха \\/Якутия\\/", EscaperType.PDF.getEscaper().apply("Саха /Якутия/"));
    }

    @Test
    public void testEscaper2() throws IOException {

        assertEquals("123\\n456\\nпричина\\n123",  EscaperType.PDF.getEscaper().apply("123\n456\nпричина\n123"));
    }
}
