package ru.gosuslugi.pgu.sp.adapter.pdf;

import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterConfigurationException;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterInputDataException;
import ru.gosuslugi.pgu.sp.adapter.pdf.converter.FormElementConverter;
import ru.gosuslugi.pgu.sp.adapter.pdf.driver.XStreamDriver;
import ru.gosuslugi.pgu.sp.adapter.types.PdfFileField;
import ru.gosuslugi.pgu.sp.adapter.util.EscapeUtil;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toCollection;
import static ru.gosuslugi.pgu.sp.adapter.types.EscaperType.XML;
import static ru.gosuslugi.pgu.sp.adapter.types.PdfSupportedClasses.FormStep;


//import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Класс для генерации pdf файлов
 */
@Service
@Slf4j
public class PdfGenerator {

    private static final String FONT_EMBED_URL_ATTR = "embed-url";
    private static final String XSLT_TEMPLATE_NAME = "/pdf/templates/pdf.xslt";
    private FopFactory fopFactory;
    private final XStream XSTREAM;

    private final static String MKS_LOGO_NAME = "/pdf/imgs/MKS_logo.png";
    private final static String SVG_LOGO_NAME = "/pdf/imgs/mark_logo.svg";
    private final static String GOSUSLUGI_HEXAGON_NAME = "/pdf/imgs/gosuslugi_hexagon.png";
    private final static String GOSUSLUGI_LOGO_NAME = "/pdf/imgs/gosuslugi_logo.png";
    private final static String DIGITAL_CIK_LOGO_NAME = "/pdf/imgs/CIU_rasp.png";
    private final static String QR_CODE_BSA = "/pdf/imgs/qr_code_for_buy_sell_agreement.png";
    private final static String QR_CODE_REG = "/pdf/imgs/qr_600100.png";

    private final static String GREEN_CIRCLE_STATUS = "/pdf/imgs/green_circle_status.png";
    private final static String YELLOW_CIRCLE_STATUS = "/pdf/imgs/yellow_circle_status.png";
    private final static String RED_CIRCLE_STATUS = "/pdf/imgs/red_circle_status.png";

    private final static String MVD_EMBLDEM = "/pdf/imgs/mvd_emblem.png";

    private final String mksLogoImageData;
    private final String svgLogo;
    private final String pngGosuslugiLogo;
    private final String pngGosuslugiHexagon;
    private final String digitalCIK;
    private final String qrCode;
    private final String qrCodeReg;
    private final String greenStatus;
    private final String yellowStatus;
    private final String redStatus;
    private final String mvdEmblem;

    public PdfGenerator() {
        try {
            mksLogoImageData = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(MKS_LOGO_NAME))));
            svgLogo = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(SVG_LOGO_NAME))));
            pngGosuslugiLogo = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(GOSUSLUGI_LOGO_NAME))));
            pngGosuslugiHexagon = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(GOSUSLUGI_HEXAGON_NAME))));
            digitalCIK = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(DIGITAL_CIK_LOGO_NAME))));
            qrCode = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(QR_CODE_BSA))));
            qrCodeReg = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(QR_CODE_REG))));
            greenStatus = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(GREEN_CIRCLE_STATUS))));
            yellowStatus = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(YELLOW_CIRCLE_STATUS))));
            redStatus = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(RED_CIRCLE_STATUS))));

            mvdEmblem = new String(Base64.encodeBase64(IOUtils.toByteArray(PdfGenerator.class.getResourceAsStream(MVD_EMBLDEM))));
        } catch (IOException e) {
            throw new SpAdapterConfigurationException("Pdf templates resources are missing",e);
        }
        try {
            DefaultConfigurationBuilder confBuilder = new DefaultConfigurationBuilder();
            DefaultConfiguration cfg = (DefaultConfiguration) confBuilder.build(PdfGenerator.class.getResource("/pdf/fonts/fop.cfg.xml").toExternalForm());
            String classPath = PdfGenerator.class.getResource("").toExternalForm();
            String packageName = PdfGenerator.class.getPackage().getName();
            int packageNameLength = packageName.length() + (!classPath.endsWith("/") && !classPath.endsWith("\\") ? 0 : 1);
            String baseUrl = classPath.substring(0, classPath.length() - packageNameLength);
            if (baseUrl.endsWith("/") || baseUrl.endsWith("\\")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }

            DefaultConfiguration fonts = (DefaultConfiguration) cfg.getChild("renderers").getChild("renderer").getChild("fonts");
            for (MutableConfiguration font : fonts.getMutableChildren("font")) {
                String embedUrl = font.getAttribute(FONT_EMBED_URL_ATTR).trim();
                String delimeter = embedUrl.startsWith("/") || embedUrl.startsWith("\\") ? "" : "/";
                String correctUrl = baseUrl + delimeter + embedUrl;
                font.setAttribute(FONT_EMBED_URL_ATTR, correctUrl);
            }
            FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(PdfGenerator.class.getResource("/pdf/fonts/fop.cfg.xml").toURI()).setConfiguration(cfg);
            fopFactory = fopFactoryBuilder.build();
        } catch (IOException | URISyntaxException e) {
            log.error("Cannot retrieve default configuration file", e);
        } catch (FOPException e) {
            log.error("Cannot create fop object", e);
        } catch (SAXException e) {
            log.error("Default configuration build error", e);
        } catch (ConfigurationException e) {
            log.error("Font properties error", e);
        }

        XSTREAM = new XStream(new XStreamDriver());
        XSTREAM.alias("model", Map.class);
        XSTREAM.registerConverter(new FormElementConverter());
    }
    public File createPdfFile(List<PdfFileField> fieldList, Map<String, String> additionalParams) {
        AtomicInteger stepsCount = new AtomicInteger(1);

        ArrayList<LinkedHashMap<String, Object>> listToDisplay = fieldList.stream().map(f -> {
            if(f.getValue()!=null) {
                f.setValue(EscapeUtil.escapeValue(f.getValue(), XML.getEscaper()).toString());
            }
            if(f.getClassName()==FormStep) {
                f.setLabel(stepsCount+". "+f.getLabel());
                stepsCount.getAndIncrement();
            }
            return f.transformToMap();
        }).collect(toCollection(ArrayList::new));
        return create(listToDisplay, additionalParams);
    }

    /**
     * Method for generation additional pdf files that are required for services
     * All data should be placed into template before using this method
     * @return
     */
    public File createVelocityPdfFile(String templateContent) {
        log.info("Generating additional pdf file");
        Map<String, String> additionalParams = new HashMap<>();
        InputStream xsltInputStream = new ByteArrayInputStream(templateContent.getBytes(StandardCharsets.UTF_8));
        InputStream xmlInputStream = new ByteArrayInputStream("<list></list>".getBytes(StandardCharsets.UTF_8));
        log.debug("Additional pdf xls content: "+ templateContent);
        try {
            return create(xsltInputStream, xmlInputStream, additionalParams);
        }catch (IOException e) {
            throw new SpAdapterInputDataException("Cannot create additional pdf file", e);
        }
    }

    private File create(Object model, Map<String, String> additionalParams) {
        try {
            File xmlPdfFile = File.createTempFile("tmp.create-pdf-xml.", ".xml");
            try(
                FileOutputStream outStream = new FileOutputStream(xmlPdfFile);
                Writer writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8)
            ) {
                XSTREAM.toXML(model, writer);
                writer.flush();
            }
            try (
                    InputStream xsltInputStream = PdfGenerator.class.getResourceAsStream(PdfGenerator.XSLT_TEMPLATE_NAME);
                    InputStream xmlInputStream = new FileInputStream(xmlPdfFile)
            ) {
                return create(xsltInputStream, xmlInputStream, additionalParams);
            }
        } catch (IOException e) {
            throw new SpAdapterInputDataException("Cannot create pdf file", e);
        }
    }

    private File create(InputStream xslt, InputStream xml, Map<String, String> additionalParams) throws IOException {
        StreamSource source = new StreamSource(xml);
        StreamSource transformSource = new StreamSource(xslt);
        File pdfFile = File.createTempFile("tmp.create-pdf.", ".pdf");

        try (FileOutputStream outStream = new FileOutputStream(pdfFile)) {
            Transformer xslfoTransformer = getTransformer(transformSource);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outStream);
            Result res = new SAXResult(fop.getDefaultHandler());

            if (xslfoTransformer != null) {
                xslfoTransformer.setParameter("mksLogoImageData", mksLogoImageData);
                xslfoTransformer.setParameter("svgLogo", svgLogo);
                xslfoTransformer.setParameter("pngGosuslugiHexagon", pngGosuslugiHexagon);
                xslfoTransformer.setParameter("pngGosuslugiLogo", pngGosuslugiLogo);
                xslfoTransformer.setParameter("digitalCIK", digitalCIK);
                xslfoTransformer.setParameter("qrCode", qrCode);
                xslfoTransformer.setParameter("qrCodeReg", qrCodeReg);
                xslfoTransformer.setParameter("greenStatus", greenStatus);
                xslfoTransformer.setParameter("yellowStatus", yellowStatus);
                xslfoTransformer.setParameter("redStatus", redStatus);
                xslfoTransformer.setParameter("mvdEmblem", mvdEmblem);
                additionalParams.forEach(xslfoTransformer::setParameter);
                xslfoTransformer.transform(source, res);
            }
        } catch (FOPException | TransformerException e) {
            throw new SpAdapterInputDataException("Cannot create fop object", e);
        }

        return pdfFile;
    }

    private Transformer getTransformer(StreamSource streamSource) {
        net.sf.saxon.TransformerFactoryImpl impl =
            new net.sf.saxon.TransformerFactoryImpl();
        try {
            return impl.newTransformer(streamSource);
        } catch (TransformerConfigurationException e) {
            log.error("Error while initialization Transformer!", e);
        }

        return null;
    }
}
