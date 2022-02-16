package ru.gosuslugi.pgu.sp.adapter.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.sp.adapter.data.SmevRequest;
import ru.gosuslugi.pgu.sp.adapter.pgu.PguClientService;
import ru.gosuslugi.pgu.sp.adapter.service.PdfPackageService;
import ru.gosuslugi.pgu.sp.adapter.service.PdfTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.service.RenderService;
import ru.gosuslugi.pgu.sp.adapter.service.ServiceProcessingClient;
import ru.gosuslugi.pgu.sp.adapter.service.SmevPdfService;
import ru.gosuslugi.pgu.sp.adapter.service.SmevService;
import ru.gosuslugi.pgu.sp.adapter.service.SmevXmlService;
import ru.gosuslugi.pgu.sp.adapter.service.TemplatesDataContextService;
import ru.gosuslugi.pgu.sp.adapter.service.XmlTemplateRenderService;
import ru.gosuslugi.pgu.sp.adapter.service.suggestion.SuggestionServiceNotifier;
import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;
import ru.gosuslugi.pgu.test.model.SpAdapterTestData;

import java.io.File;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Проверяет, логику sp-adapter, регулирующую, какие файлы отправляются в SMEV.
 */
public class SmevServiceImpl_fileAttachmentTest {
    private static final String XML_REQUEST = "<?xml>REQUEST</xml>";
    private SmevService sut;
    private SpAdapterTestData testData;
    @Mock
    private SuggestionServiceNotifier suggestionServiceNotifier;
    @Mock
    private ServiceProcessingClient spClient;
    @Mock
    private DraftClient draftClient;
    @Mock
    private TemplatesDataContextService dataContextService;
    @Mock
    private PdfPackageService pdfPackageService;
    @Mock
    private TerrabyteClient terrabyteClient;
    @Mock
    private PguClientService pguClient;
    @Mock
    private XmlValidationServiceImpl xmlValidationService;
    @Mock
    private RenderService renderService;
    @Mock
    private PdfTemplateRenderService pdfRenderService;

    @BeforeMethod
    @SneakyThrows
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testData = new SpAdapterTestData("smevServiceTest/draft.json");

        AttachmentService attachmentService = new AttachmentService(terrabyteClient);
        XmlTemplateRenderService xmlRenderService = new XmlTemplateRenderServiceImpl(renderService);
        SmevXmlService smevXmlService =
                new SmevXmlServiceImpl(xmlRenderService, attachmentService, pguClient,
                        xmlValidationService);
        SmevPdfService smevPdfService = new SmevPdfServiceImpl();
        ((SmevPdfServiceImpl) smevPdfService).setPdfTemplateRenderService(pdfRenderService);
        ((SmevPdfServiceImpl) smevPdfService).setAttachmentService(attachmentService);
        sut = spy(new SmevServiceImpl(suggestionServiceNotifier, spClient, draftClient,
                dataContextService, smevPdfService, smevXmlService, pdfPackageService,
                attachmentService));

        File file = new File(testData.getTestResource("smevServiceTest/blank.pdf").getFile());
        when(pdfRenderService.createCommonPdfAttachment(any(), any()))
                .thenReturn(file);
        when(renderService.executeTemplate(any(), anyBoolean())).thenReturn(XML_REQUEST);
        when(dataContextService.prepareRequestParameters(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(testData.getTemplateDataContext());
    }

    @Test
    public void shouldAttachBusinessAndNotAttachTransportWhenBothGiven() {
        // given
        testData.getTemplateDataContext().setFiles(
                testData.readAndDeserialize("smevServiceTest/sd-xml-business-transport-files.json",
                        new TypeReference<>() {}));

        // when
        sut.processSmevRequest(SpAdapterTestData.SERVICE_ID,
                SpAdapterTestData.ORDER_ID,
                SpAdapterTestData.OID,
                SpAdapterTestData.ROLE_ID,
                SpAdapterTestData.ORG_ID,
                testData.getDraft(), testData.isSkip17Status());

        // then
        ArgumentCaptor<SmevRequest> requestCaptor = ArgumentCaptor.forClass(SmevRequest.class);
        Mockito.verify(spClient).orderCall(requestCaptor.capture());

        SmevRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getFiles(), Matchers.not(Matchers.contains("transport_xml")));
        assertThat(capturedRequest.getFiles(), Matchers.contains("business_xml"));
    }

    @Test
    public void shouldNotAttachBusinessWhenSendToSmevForbiddenAttachmentTypeGiven() {
        // given
        testData.getTemplateDataContext().setFiles(
                testData.readAndDeserialize("smevServiceTest/sd-xml-business-forbidden-files.json",
                        new TypeReference<>() {}));

        // when
        sut.processSmevRequest(SpAdapterTestData.SERVICE_ID,
                SpAdapterTestData.ORDER_ID,
                SpAdapterTestData.OID,
                SpAdapterTestData.ROLE_ID,
                SpAdapterTestData.ORG_ID,
                testData.getDraft(), testData.isSkip17Status());

        // then
        ArgumentCaptor<SmevRequest> requestCaptor = ArgumentCaptor.forClass(SmevRequest.class);
        Mockito.verify(spClient).orderCall(requestCaptor.capture());

        SmevRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getFiles(), Matchers.not(Matchers.contains("transport_xml")));
        assertThat(capturedRequest.getFiles(), Matchers.not(Matchers.contains("business_xml_forbidden")));
        assertThat(capturedRequest.getFiles(), Matchers.contains("business_xml"));
    }

    @Test
    public void shouldAttachCommonPdfWhenLkAttachmentTypeGiven() {
        // given
        testData.getTemplateDataContext().setFiles(
                testData.readAndDeserialize("smevServiceTest/sd-pdf-common-files.json",
                        new TypeReference<>() {}));

        // when
        sut.processSmevRequest(SpAdapterTestData.SERVICE_ID,
                SpAdapterTestData.ORDER_ID,
                SpAdapterTestData.OID,
                SpAdapterTestData.ROLE_ID,
                SpAdapterTestData.ORG_ID,
                testData.getDraft(), testData.isSkip17Status());

        // then
        ArgumentCaptor<SmevRequest> requestCaptor = ArgumentCaptor.forClass(SmevRequest.class);
        Mockito.verify(spClient).orderCall(requestCaptor.capture());

        SmevRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getFiles(), Matchers.not(Matchers.contains("transport_xml")));
        assertThat(capturedRequest.getFiles(), Matchers.contains("common_pdf"));
    }

    @Test
    public void shouldNotAttachCommonPdfWhenSendToSmevForbiddenAttachmentTypeGiven() {
        // given
        testData.getTemplateDataContext().setFiles(
                testData.readAndDeserialize("smevServiceTest/sd-pdf-common-forbidden-files.json",
                        new TypeReference<>() {}));

        // when
        sut.processSmevRequest(SpAdapterTestData.SERVICE_ID,
                SpAdapterTestData.ORDER_ID,
                SpAdapterTestData.OID,
                SpAdapterTestData.ROLE_ID,
                SpAdapterTestData.ORG_ID,
                testData.getDraft(), testData.isSkip17Status());

        // then
        ArgumentCaptor<SmevRequest> requestCaptor = ArgumentCaptor.forClass(SmevRequest.class);
        Mockito.verify(spClient).orderCall(requestCaptor.capture());

        SmevRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getFiles(), Matchers.not(Matchers.contains("transport_xml")));
        assertThat(capturedRequest.getFiles(), Matchers.not(Matchers.contains("common_pdf_forbidden")));
        assertThat(capturedRequest.getFiles(), Matchers.contains("common_pdf"));
    }
}
