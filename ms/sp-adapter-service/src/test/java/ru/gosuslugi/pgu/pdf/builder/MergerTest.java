package ru.gosuslugi.pgu.pdf.builder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class MergerTest {
    @Test
    public void mergeTest() throws URISyntaxException, IOException, FileExtensionException {
        Merger merger = new Merger();
        merger.addResourceFile(getFile("img1.png"));
        merger.addResourceFile(getFile("test.pdf"));
        merger.addResourceFile(getFile("vertical.png"));
        merger.addResourceFile(getFile("horizontal.png"));
        File tmp = File.createTempFile("test_", ".pdf");
        merger.build(tmp);

        try(PDDocument testDoc = PDDocument.load(tmp)) {
            assert testDoc.getPages().getCount() == 4;
        }
    }

    @Test(expected = FileExtensionException.class)
    public void mergeFileExtErrorTest() throws URISyntaxException, IOException, FileExtensionException {
        Merger merger = new Merger();
        merger.addResourceFile(getFile("img1.png"));
        merger.addResourceFile(getFile("test.pdf"));
        merger.addResourceFile(getFile("vertical.png"));
        merger.addResourceFile(getFile("horizontal.png"));
        merger.addResourceFile(getFile("test.txt"));
    }

    private File getFile(String name) throws URISyntaxException {
        URL imageUrl = this.getClass().getResource(String.format("/pdfmerger/%s", name));
        assert imageUrl != null;
        return new File(imageUrl.toURI());
    }
}
