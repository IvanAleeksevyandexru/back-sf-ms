package ru.gosuslugi.pgu.sp.adapter.placeholder;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import ru.gosuslugi.pgu.sp.adapter.config.VelocityConfig;
import ru.gosuslugi.pgu.sp.adapter.config.props.VelocityProperties;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ContextGetCollectorPlaceholdersTest {

    private VelocityProperties properties;
    private VelocityEngine engine;
    private ContextGetCollector wrapper;


    public ContextGetCollectorPlaceholdersTest() {

        this.properties = new VelocityProperties();
        this.properties.setResourceLoader(VelocityProperties.ResourceLoader.FILE);
        this.properties.setFileResourceLoaderPath("../../epgu2-services-json/xml-templates");
        this.properties.setResourceLoaderFileCache("false");
        this.properties.setResourceLoaderFileModificationCheckInterval("0");
    }

    @Before
    public void setup() {
        engine = new VelocityConfig().velocityEngine(properties);
        VelocityContext context = new VelocityContext();
        wrapper = new ContextGetCollector(context);
    }


    @Test
    public void testOk() {
        wrapper.put("world", "World");

        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "Hello $world!");
        assertEquals("Hello World!", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertTrue("Checks", placeholders.isEmpty());
    }

    @Test
    public void testIf() {

        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "#if($key) $value1 #else $value2 #end");
        assertEquals(" $value2 ", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertFalse("Checks", placeholders.isEmpty());
        assertEquals(Arrays.asList("$value2"), placeholders);
    }

    @Test
    public void testFalse() {
        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "Hello $world!");
        assertEquals("Hello $world!", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertFalse("Checks", placeholders.isEmpty());
        assertEquals(Arrays.asList("$world"), placeholders);
    }

    @Test
    public void testMoney() {
        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "Hello $50!");
        assertEquals("Hello $50!", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertTrue("Checks", placeholders.isEmpty());
    }

    @Test
    public void testOneChar() {
        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "Hello 1 $ ");
        assertEquals("Hello 1 $ ", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertTrue("Checks", placeholders.isEmpty());
    }

    @Test
    public void testOneRusssianChar() {
        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "Hello 1 $А вы заплатили?");
        assertEquals("Hello 1 $А вы заплатили?", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertTrue("Checks", placeholders.isEmpty());
    }

    @Test
    public void testOneEnglishChar() {
        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "Hello 1 $A вы заплатили?");
        assertEquals("Hello 1 $A вы заплатили?", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertFalse("Checks", placeholders.isEmpty());
        assertEquals(Arrays.asList("$A"), placeholders);
    }

    @Test
    public void testDefaultValue() {
        StringWriter writer = new StringWriter();
        engine.evaluate(wrapper, writer, "log tag name", "Hello $!noData");
        assertEquals("Hello ", writer.toString());

        List<String> placeholders = wrapper.getLiteralKeys();

        assertNotNull(placeholders);
        assertTrue("Checks", placeholders.isEmpty());
    }
}
