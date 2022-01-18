package ru.gosuslugi.pgu.pdf.builder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileUtilsTest {
    @Test(expected = NullPointerException.class)
    public void nullTest() {
        String ext = FileUtils.getExtension(null);
    }

    @Test
    public void emptyTest() {
        String ext = FileUtils.getExtension("");
        assertNull("Ожидается null", ext);
    }

    @Test
    public void emptyExtTest() {
        String ext = FileUtils.getExtension("filename");
        assertNull("Ожидается null", ext);
    }

    @Test
    public void dotWordTest() {
        String ext = FileUtils.getExtension(".ext");
        assertEquals("Ожидается 'ext'", "ext", ext);
    }

    @Test
    public void filenameTest() {
        String ext = FileUtils.getExtension("filename.ext");
        assertEquals("Ожидается 'ext'", "ext", ext);
    }
}
