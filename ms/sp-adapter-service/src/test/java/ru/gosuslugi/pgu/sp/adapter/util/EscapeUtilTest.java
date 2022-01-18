package ru.gosuslugi.pgu.sp.adapter.util;

import org.junit.Test;
import ru.gosuslugi.pgu.sp.adapter.types.EscaperType;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class EscapeUtilTest {
    private static final String JSON_LIKE_STRING = "[removal.ai]_tmp-60cc993517bcc_auto_x2.jpg"; // EPGUCORE-64365
    private static final String UNESCAPED_JSON_LIKE_STRING = "a[\"removal.ai\"]_tmp-60cc993517bcc_auto_x2.jpg";
    private static final String ESCAPED_JSON_LIKE_STRING = "a[&quot;removal.ai&quot;]_tmp-60cc993517bcc_auto_x2.jpg";
    private static final String UNESCAPED_STRING = "Sid & Nancy";
    private static final String JSON_ARRAY = String.format("[\"%s\"]", UNESCAPED_STRING);
    private static final String JSON_OBJECT_FIELD = "film";
    private static final String JSON_OBJECT = String.format("{\"%s\": \"%s\"}", JSON_OBJECT_FIELD, UNESCAPED_STRING);
    private static final String JSON_OBJECT_WITH_ARRAY = String.format("{\"%s\": %s}", JSON_OBJECT_FIELD, JSON_ARRAY);
    private static final String ESCAPED_STRING = "Sid &amp; Nancy";
    private static final String EMPTY_STRING = "";
    private static final String STRING_STARTED_STRING = "\"Орг\"-мнацьч";

    @Test
    public void shouldEscapeWhenSimpleStringGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(UNESCAPED_STRING, EscaperType.XML.getEscaper());

        // then
        assertTrue(actual instanceof String);
        assertEquals(actual, ESCAPED_STRING);
    }

    @Test
    public void shouldReturnNullWhenNullGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(null, EscaperType.XML.getEscaper());

        // then
        assertNull(actual);
    }

    @Test
    public void shouldReturnEmptyStringWhenEmptyStringGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(EMPTY_STRING, EscaperType.XML.getEscaper());

        // then
        assertEquals(actual, EMPTY_STRING);
    }

    @Test
    public void shouldEscapeWhenJsonLikeQuotedStringGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(UNESCAPED_JSON_LIKE_STRING, EscaperType.XML.getEscaper());

        // then
        assertTrue(actual instanceof String);
        assertEquals(actual, ESCAPED_JSON_LIKE_STRING);
    }

    @Test
    public void shouldEscapeWhenJsonLikeStringGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(JSON_LIKE_STRING, EscaperType.XML.getEscaper());

        // then
        assertTrue(actual instanceof String);
        assertEquals(actual, JSON_LIKE_STRING);
    }

    @Test
    public void shouldNotEscapeWhenStartedFromStringGiven() {

        Object actual = EscapeUtil.escapeValue(STRING_STARTED_STRING, EscaperType.XML.getEscaper());

        assertTrue(actual instanceof String);
        assertEquals("&quot;Орг&quot;-мнацьч", actual);
    }

    @Test
    public void shouldConvertWhenJsonArrayGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(JSON_ARRAY, EscaperType.XML.getEscaper());

        // then
        assertTrue(actual instanceof List);
        List<?> actualList = (List<?>) actual;
        assertEquals(actualList.size(), 1);
        assertEquals(actualList.get(0), ESCAPED_STRING);
    }

    @Test
    public void shouldConvertWhenJsonObjectGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(JSON_OBJECT, EscaperType.XML.getEscaper());

        // then
        assertTrue(actual instanceof Map);
        Map<?, ?> actualMap = (Map<?, ?>) actual;
        assertEquals(actualMap.size(), 1);
        assertTrue(actualMap.containsKey(JSON_OBJECT_FIELD));
        assertEquals(actualMap.get(JSON_OBJECT_FIELD), ESCAPED_STRING);
    }

    @Test
    public void shouldTraverseWhenJsonObjectWithArrayGiven() {
        // given
        // when
        Object actual = EscapeUtil.escapeValue(JSON_OBJECT_WITH_ARRAY, EscaperType.XML.getEscaper());

        // then
        assertTrue(actual instanceof Map);
        Map<?, ?> actualMap = (Map<?, ?>) actual;
        assertEquals(actualMap.size(), 1);
        assertTrue(actualMap.containsKey(JSON_OBJECT_FIELD));
        final Object objectFieldValue = actualMap.get(JSON_OBJECT_FIELD);
        assertTrue(objectFieldValue instanceof List);
        List<?> objectFieldList = (List<?>) objectFieldValue;
        assertEquals(objectFieldList.size(), 1);
        assertEquals(objectFieldList.get(0), ESCAPED_STRING);
    }
}
