package ru.gosuslugi.pgu.sp.adapter.service;//package ru.gosuslugi.pgu.sp.adapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.dto.pdf.data.FileType;
import ru.gosuslugi.pgu.dto.pdf.data.UniqueType;
import ru.gosuslugi.pgu.sp.adapter.client.SpServiceDescriptorClient;
import ru.gosuslugi.pgu.sp.adapter.config.VelocityConfig;
import ru.gosuslugi.pgu.sp.adapter.config.props.VelocityProperties;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;
import ru.gosuslugi.pgu.sp.adapter.pdf.PdfGenerator;
import ru.gosuslugi.pgu.sp.adapter.service.impl.PdfTemplateRenderServiceImpl;
import ru.gosuslugi.pgu.sp.adapter.service.impl.RenderServiceImpl;
import ru.gosuslugi.pgu.sp.adapter.service.impl.TemplatePackageLocalServiceImpl;
import ru.gosuslugi.pgu.sp.adapter.service.impl.TemplatesDataContextServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.*;
import static ru.gosuslugi.pgu.sp.adapter.service.XMLServiceBatchTest.getServiceDescriptorClient;
import static ru.gosuslugi.pgu.sp.adapter.util.DefaultOptionsSpConfig.determineTemplateFileName;

public abstract class AddPdfServiceBatchTest {
    public static final String JSON_TEMPLATES = "../../epgu2-services-json";
    public static final String XML_TEMPLATES = "../../epgu2-services-json/xml-templates";

    private static final ObjectMapper OBJECT_MAPPER = JsonProcessingUtil.getObjectMapper();

    private String roleId;
    private Long oid;
    private String directory;
    private TestPdfMode testPdfMode;

    private final String scenarioJson;

    private VelocityProperties properties;


    public static Object[][] sumTestData() {
        return null;
    }

    public AddPdfServiceBatchTest(String roleId, Long oid, String directory, TestPdfMode testPdfMode) throws IOException {
        this.roleId = roleId;
        this.oid = oid;
        this.directory = directory;
        this.testPdfMode = testPdfMode;

        this.scenarioJson = getExternalFileAsString(JSON_TEMPLATES + "/" + getScenarioFilename());

        this.properties = new VelocityProperties();
        this.properties.setResourceLoader(VelocityProperties.ResourceLoader.FILE);
        this.properties.setFileResourceLoaderPath(XML_TEMPLATES);
        this.properties.setResourceLoaderFileCache("false");
        this.properties.setResourceLoaderFileModificationCheckInterval("0");
    }

    protected abstract String getScenarioFilename();

    @Ignore
    @Test
    public void testPdf() throws IOException {
        TemplatesDataContextService service = getTemplatesDataContextServiceImpl();
        RenderService renderService = getRenderService();
        PdfTemplateRenderService pdfTemplateRenderService = getPdfTemplateRenderService(renderService);

        String draftFileName = directory + "/draft.json";
        DraftHolderDto draft = getDraftFromFile(draftFileName);
        assertNotNull(draft);


        TemplatesDataContext templatesDataContext = service.prepareRequestParameters(draft.getType(), draft.getOrderId(), oid, roleId, draft.getBody(), null, false);
        FileDescription pdfFileDescription = new FileDescription();
        pdfFileDescription.setType(FileType.PDF);
        pdfFileDescription.setAddedFileName(UniqueType.NONE);
        String folderAdditionalPdf = templatesDataContext.getServiceId() + "/additional";
        Map<ApplicantRole, String> templatesPdfAdditional = Map.of(
                ApplicantRole.valueOf(roleId), determineTemplateFileName(folderAdditionalPdf, templatesDataContext.getServiceId(), ApplicantRole.valueOf(roleId), getPdfPrefix())
        );
        pdfFileDescription.setTemplates(templatesPdfAdditional);

        // Generates PDF
        File pdfFile = pdfTemplateRenderService.createPdfAttachnment(templatesDataContext, pdfFileDescription);

        assertNotNull(pdfFile);
        // To Open
        System.out.println("Pdf File: " + pdfFile.getAbsolutePath());
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Runtime.getRuntime().exec(new String[]{"cmd.exe", "/C", pdfFile.getAbsolutePath()});
        }

        String pdfFileName = directory + "/expected.pdf";
        if (this.testPdfMode.isGenerate()) {
            FileUtils.copyFile(pdfFile, new File("src/test/resources/" + pdfFileName));
        } else {
            PDDocument expectedPd = PDDocument.load (getClass().getClassLoader().getResourceAsStream(pdfFileName));
            PDDocument pd = PDDocument.load (pdfFile);
            assertEquals("Check counts of pages for " + directory, expectedPd.getNumberOfPages(), pd.getNumberOfPages());

            PDFRenderer expectedPr = new PDFRenderer(expectedPd);
            PDFRenderer pr = new PDFRenderer(pd);

            // Generates jpegs and checks
            for (int page = 0; page < pd.getNumberOfPages(); page++) {

                // Generate expected jpeg
                String excectedJpegFileName = "tmp.create-jpeg.expected." + directory + "." + (page + 1) + ".";
                File expectedJpeg = getjpegFile(expectedPr, page, excectedJpegFileName);

                // Generate actual jpeg
                String jpegFileName = "tmp.create-jpeg.actual." + directory + "." + (page + 1) + ".";
                File jpeg = getjpegFile(pr, page, jpegFileName);

                assertTrue(
                    "Checking expected " + expectedJpeg.getAbsolutePath() + " vs actual " + jpeg.getAbsolutePath(),
                    FileUtils.contentEquals(expectedJpeg, jpeg)
                );
            }
        }
    }

    private RenderService getRenderService() {
        return new RenderServiceImpl(
            new VelocityConfig().velocityEngine(properties),
            new TemplatePackageLocalServiceImpl(),
            new VelocityConfig().prototypeTemplateContext()
        );
    }

    private PdfTemplateRenderService getPdfTemplateRenderService(RenderService renderService) {
        PdfTemplateRenderService result = new PdfTemplateRenderServiceImpl(renderService, new PdfGenerator(), OBJECT_MAPPER);
        return result;
    }


    protected abstract String getPdfPrefix();

    private File getjpegFile(PDFRenderer pr, int page, String excpectedJpegFileName) throws IOException {
        File expectedJpeg = File.createTempFile(excpectedJpegFileName, ".jpeg");
        BufferedImage bi = pr.renderImageWithDPI(page, 300);
        ImageIO.write(bi, "JPEG", expectedJpeg);
        return expectedJpeg;
    }

    private TemplatesDataContextService getTemplatesDataContextServiceImpl() {
        AttachmentService attachmentService = Mockito.mock(AttachmentService.class);
        SpServiceDescriptorClient spServiceDescriptorClient = Mockito.mock(SpServiceDescriptorClient.class);
        return new TemplatesDataContextServiceImpl(OBJECT_MAPPER, attachmentService, getServiceDescriptorClient(scenarioJson), spServiceDescriptorClient);
    }

    private DraftHolderDto getDraftFromFile(String fileName) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);) {
            return OBJECT_MAPPER.readValue(
                is,
                DraftHolderDto.class
            );
        }
    }

    private String getExternalFileAsString(String fileName) throws IOException {
        try (InputStream is = new FileInputStream(fileName)) {
            try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                return IOUtils.toString(reader);
            }
        }
    }
}
