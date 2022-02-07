package ru.gosuslugi.pgu.xmlservice.context.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.common.core.exception.EntityNotFoundException;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.descriptor.ServiceDescriptor;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.fs.common.service.JsonProcessingService;
import ru.gosuslugi.pgu.fs.common.service.impl.JsonProcessingServiceImpl;
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient;
import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.context.service.TemplateDataService;
import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.AssertJUnit.assertTrue;

public class TemplateDataServiceImplTest {
    private static final String SOURCES_ROOT = "context/";
    private static final String SERVICE_ID = "1";
    private static final long ORDER_ID = 2L;
    private static final long USER_OID = 3L;
    private static final long ORG_ID = 4L;
    private static final String ROLE_ID = "5";
    private static final String SIMPLE_ANSWER_KEY = "simpleAnswer";
    private static final String SIMPLE_ANSWER_VALUE = "Simple Answer";
    private static final String PARAM_VALUE = "Simple Param";
    private static final String PARAM_KEY = "param";
    private static final String JSON_ARRAY_ANSWER_KEY = "jsonArrayAnswer";
    private static final String JSON_ARRAY_ANSWER_VALUE = "Valid Json Array";
    private static final String JSON_OBJ_ANSWER_KEY = "jsonObjectAnswer";
    private static final String JSON_OBJ_ANSWER_VALUE = "Valid Json Object";
    private static final String JSON_OBJ_ANSWER_FIELD = "field";
    private static final String JSON_INVALID_VALUE = "{\"field\": \"Invalid Json Object}";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static final ScenarioDto DRAFT = new ScenarioDto();
    private TemplateDataService sut;
    @Mock
    private ServiceDescriptorClient sdClient;
    @Spy
    private DraftClient draftClient = Mockito.mock(DraftClient.class);
    @Mock
    private AttachmentService attachmentService;
    private GenerateXmlRequest context;
    private JsonProcessingService jsonProcessingService;

    private static <T> void assumeThat(T actual, Matcher<? super T> matcher) {
        if (!matcher.matches(actual)) {
            throw new SkipException("");
        }
    }

    private static void assumeFalse(boolean condition) {
        if (condition) {
            throw new SkipException("");
        }
    }

    private static void assumeTrue(boolean condition) {
        if (!condition) {
            throw new SkipException("");
        }
    }

    @BeforeClass
    public void initClass() {
        jsonProcessingService = new JsonProcessingServiceImpl(OBJECT_MAPPER);
    }

    @BeforeMethod
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);

        DraftHolderDto draftDtoStub = readAndDeserialize("draft.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID, ORG_ID))
                .thenReturn(draftDtoStub);
        var serviceDescriptionStub = jsonProcessingService.fromJson(read("service-description.json"), ServiceDescriptor.class);
        Mockito.when(sdClient.getServiceDescriptor(SERVICE_ID)).thenReturn(serviceDescriptionStub);

        sut = new TemplateDataServiceImpl(sdClient, draftClient, attachmentService);

        createContext(USER_OID);

        DRAFT.setServiceDescriptorId(SERVICE_ID);
    }

    private void createContext(final long userOid) {
        context = new GenerateXmlRequest(SERVICE_ID, ORDER_ID, userOid, ROLE_ID,
                new FileDescription());
        context.setOrgId(ORG_ID);
    }

    @Test
    public void shouldInitContextWhenRequestGiven() {
        // given
        assumeThat(context.getServiceId(), is(SERVICE_ID));
        assumeThat(context.getOrderId(), is(ORDER_ID));
        assumeThat(context.getUserId(), is(USER_OID));

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        assertThat(actual.getServiceId(), is(SERVICE_ID));
        assertThat(actual.getOrderId(), is(ORDER_ID));
        assertThat(actual.getOid(), is(USER_OID));
    }

    @Test
    public void shouldPopulateContextWhenServiceDescriptionParametersGiven() throws IOException {
        // given
        var serviceDescriptionStub = jsonProcessingService.fromJson(read("service-description-params.json"), ServiceDescriptor.class);
        Mockito.when(sdClient.getServiceDescriptor(SERVICE_ID)).thenReturn(serviceDescriptionStub);

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, String> serviceParameters = actual.getServiceParameters();
        assertThat(serviceParameters.size(), is(greaterThanOrEqualTo(1)));
        assertTrue(serviceParameters.containsKey(PARAM_KEY));
        assertThat(serviceParameters.get(PARAM_KEY), is(PARAM_VALUE));
    }

    @Test
    public void shouldPopulateWhenSimpleStringApplicantAnswerGiven() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-simple-str-answer.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID, ORG_ID)).thenReturn(draftStub);
        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> values = actual.getValues();
        assertThat(values.size(), is(greaterThanOrEqualTo(1)));
        assertTrue(values.containsKey(SIMPLE_ANSWER_KEY));
        assertThat(values.get(SIMPLE_ANSWER_KEY), is(SIMPLE_ANSWER_VALUE));
    }

    @Test
    public void shouldPopulateCurrentValueWhenApplicantAnswersEmpty() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-current-value.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID, ORG_ID)).thenReturn(draftStub);
        assumeFalse(draftStub.getBody().getCurrentValue().isEmpty());
        assumeTrue(draftStub.getBody().getApplicantAnswers().isEmpty());
        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> values = actual.getValues();
        assertThat(values.size(), is(greaterThanOrEqualTo(1)));
        assertTrue(values.containsKey(SIMPLE_ANSWER_KEY));
        assertThat(values.get(SIMPLE_ANSWER_KEY), is(SIMPLE_ANSWER_VALUE));
    }

    @Test
    public void shouldPopulateWhenArrayOrObjectApplicantAnswerGiven() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-arr-obj-answer.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID, ORG_ID)).thenReturn(draftStub);

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> values = actual.getValues();
        assertThat(values.size(), is(greaterThanOrEqualTo(1)));

        assertTrue(values.containsKey(JSON_OBJ_ANSWER_KEY));
        final Object objAnswerRaw = values.get(JSON_OBJ_ANSWER_KEY);
        assertThat(objAnswerRaw, is(instanceOf(Map.class)));
        Map objAnswer = (Map) objAnswerRaw;
        assertTrue(objAnswer.containsKey(JSON_OBJ_ANSWER_FIELD));
        assertThat(objAnswer.get(JSON_OBJ_ANSWER_FIELD), is(JSON_OBJ_ANSWER_VALUE));

        final Object answerRaw = values.get(JSON_ARRAY_ANSWER_KEY);
        assertThat(answerRaw, is(instanceOf(List.class)));
        final List answer = (List) answerRaw;
        assertThat(answer.size(), is(1));
        assertThat((String) answer.get(0), is(JSON_ARRAY_ANSWER_VALUE));
    }

    @Test
    public void shouldPopulateAsIsWhenInvalidJsonApplicantAnswerGiven() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-invalid-json-answer.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID, ORG_ID)).thenReturn(draftStub);

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> values = actual.getValues();
        assertThat(values.size(), is(greaterThanOrEqualTo(1)));

        assertTrue(values.containsKey(JSON_OBJ_ANSWER_KEY));
        final Object invalidJsonAnswerRaw = values.get(JSON_OBJ_ANSWER_KEY);
        assertThat(invalidJsonAnswerRaw, is(instanceOf(String.class)));
        assertThat((String) invalidJsonAnswerRaw, is(JSON_INVALID_VALUE));
    }

    @Test(expectedExceptions = EntityNotFoundException.class)
    public void shouldThrowWhenDraftHolderBodyIsNull() {
        // given
        DraftHolderDto draftStub = new DraftHolderDto();
        draftStub.setBody(null);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID, ORG_ID)).thenReturn(draftStub);

        // when
        sut.prepare(context);
        // then
        AssertJUnit.fail();
    }

    @Test(expectedExceptions = EntityNotFoundException.class)
    public void shouldThrowWhenDraftHolderNotFound() {
        // given
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID, ORG_ID)).thenReturn(null);

        // when
        sut.prepare(context);
        // then
        AssertJUnit.fail();
    }

    @Test
    public void shouldLoadDraftWhenItNotProvided() {
        // given
        assumeThat(context.getDraft(), is(nullValue()));
        // when
        sut.prepare(context);
        // then
        Mockito.verify(draftClient).getDraftById(ORDER_ID, USER_OID, ORG_ID);
    }

    @Test
    public void shouldNotCallDraftClientWhenDraftProvided() {
        // given
        context.setDraft(DRAFT);
        // when
        sut.prepare(context);
        // then
        Mockito.verify(draftClient, Mockito.times(0)).getDraftById(ORDER_ID, USER_OID, ORG_ID);
    }

    private <T> T readAndDeserialize(String fileName, Class<T> targetClass) throws IOException {
        final URL resource = getTestResource(fileName);
        return OBJECT_MAPPER.readValue(resource, targetClass);
    }

    private String read(String fileName) throws IOException {
        return IOUtils.toString(getTestResource(fileName));
    }

    private URL getTestResource(String fileName) {
        final String filePath = SOURCES_ROOT + fileName;
        return getClass().getClassLoader().getResource(filePath);
    }
}
