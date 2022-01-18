package ru.gosuslugi.pgu.pdf.builder;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceFileTest {
    @Test
    public void createImgTest() throws URISyntaxException, FileExtensionException {
        URL imageUrl = this.getClass().getResource("/pdfmerger/img1.png");
        assert imageUrl != null;
        File image = new File(imageUrl.toURI());
        ResourceFile resource = new ResourceFile(image);
        assert resource.type == ResourceType.IMAGE;
        assert resource.file == image;
    }

    @Test
    public void createPdfTest() throws URISyntaxException, FileExtensionException {
        URL imageUrl = this.getClass().getResource("/pdfmerger/test.pdf");
        assert imageUrl != null;
        File image = new File(imageUrl.toURI());
        ResourceFile resource = new ResourceFile(image);
        assert resource.type == ResourceType.PDF;
        assert resource.file == image;
    }

    @Test(expected = FileExtensionException.class)
    public void extError() throws URISyntaxException, FileExtensionException {
        URL imageUrl = this.getClass().getResource("/pdfmerger/test.txt");
        assert imageUrl != null;
        File image = new File(imageUrl.toURI());
        ResourceFile resource = new ResourceFile(image);
    }
}
