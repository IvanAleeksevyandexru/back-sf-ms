package ru.gosuslugi.pgu.draftconverter.context.service.impl;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import ru.gosuslugi.pgu.draftconverter.context.service.ParseService;
import ru.gosuslugi.pgu.draftconverter.data.XmlAttr;
import ru.gosuslugi.pgu.draftconverter.data.XmlElement;
import ru.gosuslugi.pgu.draftconverter.testutil.IoUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class XmlParseDomImplTest {
    private static final ParseService<XmlElement> SUT = new XmlParseDomImpl();
    private static final String ATTR1_NAME = "attr1";
    private static final String ATTR2_NAME = "attr2";
    private static final String ATTR3_NAME = "attr3";
    private static final String TAG1_NAME = "tag1";
    private static final String TAG2_NAME = "tag2";
    private static final String VALUE_1 = "value1";
    private static final String VALUE_2 = "value2";
    private static final String VALUE_3 = "value3";
    private static final IoUtil IO_UTIL = IoUtil.getInstance();

    @Test
    public void shouldParseEmptyXmlWhenRootTagOnlyGiven() throws IOException {
        // given
        final String xml = IO_UTIL.read("minimal.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        // then
        assertEquals(actual.childrenCount(), 0);
        assertEquals(actual.attrsCount(), 0);
    }

    @Test
    public void shouldReturnNullWhenTagAbsent() throws IOException {
        // given
        final String xml = IO_UTIL.read("minimal.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        // then
        assertNull(actual.get(0));
        assertNull(actual.get(TAG1_NAME));
    }

    @Test
    public void shouldReturnNullWhenAttrAbsent() throws IOException {
        // given
        final String xml = IO_UTIL.read("minimal.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        // then
        assertNull(actual.attr(0));
        assertNull(actual.attr(TAG1_NAME));
    }

    @Test
    public void shouldParseXmlAttrsWhenTagWithAttrsGiven() throws IOException {
        // given
        final String xml = IO_UTIL.read("attrs.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        // then
        assertEquals(actual.childrenCount(), 0);
        checkAttrsCount(actual, 2);

        checkAndGetAttr(actual, 0, ATTR1_NAME, VALUE_1);
        checkAndGetAttr(actual, 1, ATTR2_NAME, VALUE_2);
    }

    @Test
    public void shouldParseXmlAttrsWhenChildrenWithAttrsGiven() throws IOException {
        // given
        final String xml = IO_UTIL.read("list-attrs.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        // then
        checkChildrenCount(actual, 2);
        assertEquals(actual.attrsCount(), 0);

        XmlElement tag1 = checkAndGetChild(actual, 0, TAG1_NAME, VALUE_1);
        checkAndGetAttr(tag1, 0, ATTR1_NAME, VALUE_1);
        checkAndGetAttr(tag1, 1, ATTR2_NAME, VALUE_2);

        XmlElement tag2 = checkAndGetChild(actual, 1, TAG1_NAME, VALUE_2);
        checkAndGetAttr(tag2, 0, ATTR2_NAME, VALUE_2);
        checkAndGetAttr(tag2, 1, ATTR3_NAME, VALUE_3);
    }

    @Test
    public void shouldReturnElementTextWhenToStringCalled() throws IOException {
        // given
        final String xml = IO_UTIL.read("nested-tags.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        // then
        checkChildrenCount(actual, 2);

        XmlElement tag1 = checkAndGetChild(actual, 0, TAG1_NAME, VALUE_1);
        assertEquals("" + tag1, VALUE_1);
    }

    @Test
    public void shouldReturnAttrValueWhenToStringCalled() throws IOException {
        // given
        final String xml = IO_UTIL.read("attrs.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        // then
        checkAttrsCount(actual, 2);
        XmlAttr attr1 = checkAndGetAttr(actual, 0, ATTR1_NAME, VALUE_1);
        assertEquals("" + attr1, VALUE_1);
    }

    @Test
    public void shouldParseTreeWhenNestedTagsGiven() throws IOException {
        // given
        final String xml = IO_UTIL.read("nested-tags.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        checkChildrenCount(actual, 2);
        assertEquals(actual.attrsCount(), 0);

        checkAndGetChild(actual, 0, TAG1_NAME, VALUE_1);
        checkAndGetChild(actual, 1, TAG2_NAME, VALUE_2);
    }


    @Test
    public void shouldParseListWhenSameSiblingsGiven() throws IOException {
        // given
        final String xml = IO_UTIL.read("same-tags.xml");

        // when
        XmlElement actual = SUT.parse(xml);

        checkChildrenCount(actual, 2);
        assertEquals(actual.attrsCount(), 0);

        final XmlElement firstTag = actual.get(TAG1_NAME);
        assertNotNull(firstTag);
        assertEquals(firstTag.getText(), VALUE_1);

        final List<XmlElement> sameNameTags = actual.children(TAG1_NAME);
        assertEquals(sameNameTags.size(), 2);
        final XmlElement firstFromList = sameNameTags.get(0);
        assertNotNull(firstFromList);
        assertEquals(firstFromList, firstTag);
        final XmlElement secondFromList = sameNameTags.get(1);
        assertNotNull(secondFromList);
        assertEquals(secondFromList.getText(), VALUE_2);
    }

    private void checkChildrenCount(XmlElement node, final int count) {
        assertEquals(node.childrenCount(), count);
        final List<XmlElement> children = node.children();
        assertNotNull(children);
        assertEquals(children.size(), node.childrenCount());
    }

    private void checkAttrsCount(XmlElement node, final int count) {
        assertEquals(node.attrsCount(), count);
        final List<XmlAttr> attrs = node.attrs();
        assertNotNull(attrs);
        assertEquals(attrs.size(), node.attrsCount());
    }

    private XmlElement checkAndGetChild(XmlElement node, final int idx, final String name, final String value) {
        final XmlElement childByIndex = node.get(idx);
        assertNotNull(childByIndex);
        assertEquals(childByIndex.getName(), name);
        assertEquals(childByIndex.getText(), value);
        assertEquals(childByIndex.toString(), value);
        return childByIndex;
    }

    private XmlAttr checkAndGetAttr(XmlElement node, final int idx, final String name, final String value) {
        final XmlAttr attrByIdx = node.attr(idx);
        assertNotNull(attrByIdx);
        final XmlAttr attrByName = node.attr(name);
        assertEquals(attrByIdx, attrByName);
        assertEquals(attrByIdx.getName(), name);
        assertEquals(attrByIdx.getValue(), value);
        assertEquals(attrByIdx.toString(), value);
        return attrByIdx;
    }
}
