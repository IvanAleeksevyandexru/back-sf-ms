package ru.gosuslugi.pgu.draftconverter.context.service.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import ru.gosuslugi.pgu.draftconverter.context.service.ParseService;
import ru.gosuslugi.pgu.draftconverter.testutil.IoUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

public class JsonParseImplTest {
    private static final ParseService<Object> SUT = new JsonParseImpl();
    private static final String KG_KEY = "kg215";
    private static final String NAME_KEY = "name";
    private static final String NAME_VALUE = "Детский сад Солнышко";
    private static final String CODE_KEY = "codes";
    private static final List<Integer> CODES_VALUE = Arrays.asList(215, 217);
    private static final IoUtil IO_UTIL = IoUtil.getInstance();

    @Test
    public void shouldParseMapWhenJsonStringGiven() throws IOException {
        // given
        final String json = IO_UTIL.read("sample.json");

        // when
        Object actual = SUT.parse(json);

        // then
        final Object kg215Obj = checkAndGetValue(actual, KG_KEY);
        final Object name = checkAndGetValue(kg215Obj, NAME_KEY);
        assertEquals(name, NAME_VALUE);
        final Object code = checkAndGetValue(kg215Obj, CODE_KEY);
        assertEquals(code, CODES_VALUE);
    }


    private Object checkAndGetValue(Object obj, final String key) {
        assertNotNull(obj);
        assertTrue(Map.class.isAssignableFrom(obj.getClass()));
        Map<?, ?> map = (Map<?, ?>) obj;
        final Object value = map.get(key);
        assertNotNull(value);
        return value;
    }
}
