package ru.gosuslugi.pgu.sp.adapter.placeholder;

import org.apache.velocity.VelocityContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class ContextGetCollectorTest {

    @Test
    public void testNoValues() {
        VelocityContext context = new VelocityContext();

        List<String> nullKeys = new ContextGetCollector(context).getUsedKeys();

        assertNotNull(nullKeys);
        assertTrue("Checks", nullKeys.isEmpty());
    }

    @Test
    public void testValue() {
        VelocityContext context = new VelocityContext();
        String key = "key";
        String value = "value";
        String key1 = "key1";
        context.put(key, value);
        ContextGetCollector contextGetCollector = new ContextGetCollector(context);

        assertEquals(value, contextGetCollector.get(key));
        assertNull(contextGetCollector.get(key1));

        List<String> nullKeys = contextGetCollector.getUsedKeys();
        assertNotNull(nullKeys);
        assertFalse("Checks", nullKeys.isEmpty());
        assertEquals(Arrays.asList(key, key1), nullKeys);
    }
}
