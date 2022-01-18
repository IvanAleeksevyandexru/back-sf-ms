package ru.gosuslugi.pgu.draftconverter.testutil;

import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.IOUtils;

public class IoUtil {
    private static IoUtil instance;
    private static final String SOURCES_ROOT = "context/";
    private IoUtil() {
    }

    public static IoUtil getInstance() {
        if (instance == null) {
            instance = new IoUtil();
        }
        return instance;
    }

    public String read(String fileName) throws IOException {
        return IOUtils.toString(getTestResource(fileName));
    }

    private URL getTestResource(String fileName) {
        final String filePath = SOURCES_ROOT + fileName;
        return getClass().getClassLoader().getResource(filePath);
    }
}
