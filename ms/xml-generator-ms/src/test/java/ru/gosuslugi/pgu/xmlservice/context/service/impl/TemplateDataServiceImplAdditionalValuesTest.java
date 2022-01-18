package ru.gosuslugi.pgu.xmlservice.context.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.draft.DraftClient;
import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ScenarioDto;
import ru.gosuslugi.pgu.dto.pdf.data.FileDescription;
import ru.gosuslugi.pgu.sd.storage.ServiceDescriptorClient;
import ru.gosuslugi.pgu.xmlservice.context.data.TemplateDataContext;
import ru.gosuslugi.pgu.xmlservice.context.service.TemplateDataService;
import ru.gosuslugi.pgu.xmlservice.data.GenerateXmlRequest;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TemplateDataServiceImplAdditionalValuesTest {
    private static final String SOURCES_ROOT = "context/";
    private static final String SERVICE_ID = "1";
    private static final long ORDER_ID = 2L;
    private static final long USER_OID_VALID_COMPONENT = 3L;
    private static final long USER_OID_NON_EXISTING_COMPONENT = 2L;
    private static final long USER_OID_NULL_VALUE_COMPONENT = 1L;
    private static final long USER_OID_NON_JSON_VALUE_COMPONENT = 4L;
    private static final long USER_OID_NON_JSON_ARRAY_VALUE_COMPONENT = 5L;
    private static final long USER_OID_JSON_ARRAY_WITH_NON_OBJ_VALUE_COMPONENT = 6L;
    private static final long USER_OID_JSON_ARRAY_WITH_NON_EXISTING_INDEX_VALUE_COMPONENT = 7L;
    private static final long ORG_ID = 4L;
    private static final String ROLE_ID = "5";
    private static final String PARAM_VALUE = "Simple Param";
    private static final String PARAM_KEY = "param";
    private static final String SP_REQUEST_GUID_KEY = "sp_request_guid";
    private static final String SP_REQUEST_HASH_KEY = "sp_request_hash";
    private static final String SP_REQUEST_GUID_VALUE = "sp_request_guid";
    private static final String SP_REQUEST_HASH_VALUE = "sp_request_hash";
    private static final String DIGEST_KEY = "digestKey";
    private static final String DIGEST_VALUE = "digestValue";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static final ScenarioDto DRAFT = new ScenarioDto();
    protected static final String CYCLED_FIRST_NAME_KEY = "cycledFirstName";
    protected static final String CYCLED_FIRST_NAME_VALUE = "Вероника";
    private TemplateDataService sut;
    @Mock
    private ServiceDescriptorClient sdClient;
    @Spy
    private DraftClient draftClient = Mockito.mock(DraftClient.class);
    @Mock
    private AttachmentService attachmentService;
    private GenerateXmlRequest context;

    private static void assumeFalse(boolean condition) {
        if (condition) {
            throw new SkipException("");
        }
    }

    @BeforeMethod
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);

        DraftHolderDto draftDtoStub = readAndDeserialize("draft.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID_VALID_COMPONENT, ORG_ID))
                .thenReturn(draftDtoStub);
        String serviceDescriptionStub = read("service-description.json");
        Mockito.when(sdClient.getServiceDescriptor(SERVICE_ID)).thenReturn(serviceDescriptionStub);

        sut = new TemplateDataServiceImpl(sdClient, draftClient, attachmentService);

        createContext(USER_OID_VALID_COMPONENT);

        DRAFT.setServiceDescriptorId(SERVICE_ID);
    }

    private void createContext(final long userOid) {
        context = new GenerateXmlRequest(SERVICE_ID, ORDER_ID, userOid, ROLE_ID,
                new FileDescription());
        context.setOrgId(ORG_ID);
    }

    @Test
    public void shouldPopulateWhenAdditionalParamsGiven() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-additional-params.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID_VALID_COMPONENT, ORG_ID))
                .thenReturn(draftStub);

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> additionalValues = actual.getAdditionalValues();
        assertThat(additionalValues.size(), is(greaterThanOrEqualTo(1)));

        assertTrue(additionalValues.containsKey(PARAM_KEY));
        assertThat((String) additionalValues.get(PARAM_KEY), is(PARAM_VALUE));
    }

    @Test
    public void shouldLeaveSpRequestGuidAndHashWhenAdditionalParamsGiven() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-additional-params-sp.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID_VALID_COMPONENT, ORG_ID))
                .thenReturn(draftStub);

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> additionalValues = actual.getAdditionalValues();
        assertThat(additionalValues.size(), is(greaterThanOrEqualTo(2)));

        assertTrue(additionalValues.containsKey(SP_REQUEST_GUID_KEY));
        assertThat((String) additionalValues.get(SP_REQUEST_GUID_KEY), is(SP_REQUEST_GUID_VALUE));

        assertTrue(additionalValues.containsKey(SP_REQUEST_HASH_KEY));
        assertThat((String) additionalValues.get(SP_REQUEST_HASH_KEY), is(SP_REQUEST_HASH_VALUE));
    }

    @Test
    public void shouldGenerateSpRequestGuidAndHashWhenTheyAbsent() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-additional-params.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID_VALID_COMPONENT, ORG_ID))
                .thenReturn(draftStub);
        assumeFalse(draftStub.getBody().getAdditionalParameters().containsKey(SP_REQUEST_GUID_KEY));
        assumeFalse(draftStub.getBody().getAdditionalParameters().containsKey(SP_REQUEST_HASH_KEY));

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> additionalValues = actual.getAdditionalValues();
        assertThat(additionalValues.size(), is(greaterThanOrEqualTo(2)));

        assertTrue(additionalValues.containsKey(SP_REQUEST_GUID_KEY));
        assertNotNull(additionalValues.get(SP_REQUEST_GUID_KEY));

        assertTrue(additionalValues.containsKey(SP_REQUEST_HASH_KEY));
        assertNotNull(additionalValues.get(SP_REQUEST_HASH_KEY));
    }

    @Test
    public void shouldPopulateWithCycledApplicantAnswersWhenItPresent() throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-cycled-component.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, USER_OID_VALID_COMPONENT, ORG_ID))
                .thenReturn(draftStub);

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> additionalValues = actual.getAdditionalValues();
        assertThat(additionalValues.size(), is(greaterThanOrEqualTo(1)));

        assertTrue(additionalValues.containsKey(CYCLED_FIRST_NAME_KEY));
        verifyRoleIndexAndComponentValues(additionalValues, "cycledComponent", 1);
        assertThat(additionalValues.get(CYCLED_FIRST_NAME_KEY), is(CYCLED_FIRST_NAME_VALUE));
        Mockito.verify(attachmentService)
                .getAttachmentsDigestValues(ArgumentMatchers.any(ScenarioDto.class),
                        ArgumentMatchers.anyBoolean());
    }

    private void verifyRoleIndexAndComponentValues(Map<String, Object> additionalValues,
            final String componentName, final Integer index) {
        assertThat(additionalValues.get(TemplateDataContext.ROLE_INDEX_KEY), is(index));
        assertThat(additionalValues.get(TemplateDataContext.ROLE_COMPONENT_KEY), is(componentName));
    }

    @DataProvider
    public static Object[][] userOids() {
        return new Object[][]{{USER_OID_NON_EXISTING_COMPONENT, "nonexistentCycledComponent", 1},
                {USER_OID_NULL_VALUE_COMPONENT, "nullValueComponent", 1},
                {USER_OID_NON_JSON_VALUE_COMPONENT, "nonJsonValueComponent", 1},
                {USER_OID_NON_JSON_ARRAY_VALUE_COMPONENT, "nonJsonArrayValueComponent", 1},
                {USER_OID_JSON_ARRAY_WITH_NON_OBJ_VALUE_COMPONENT,
                        "jsonArrayWithNonObjValueComponent", 0},
                {USER_OID_JSON_ARRAY_WITH_NON_EXISTING_INDEX_VALUE_COMPONENT,
                        "jsonArrayWithNonExistingIndex", 1},};
    }

    @Test(dataProvider = "userOids")
    public void shouldSkipProcessingCycledApplicantAnswersWhenComponentCannotBeResolved(
            long userOid, String expectedComponentName, Integer expectedComponentIndex)
            throws IOException {
        // given
        DraftHolderDto draftStub =
                readAndDeserialize("draft-cycled-component.json", DraftHolderDto.class);
        Mockito.when(draftClient.getDraftById(ORDER_ID, userOid, ORG_ID)).thenReturn(draftStub);
        createContext(userOid);

        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> additionalValues = actual.getAdditionalValues();

        assertFalse(additionalValues.containsKey(CYCLED_FIRST_NAME_KEY));
        verifyRoleIndexAndComponentValues(additionalValues, expectedComponentName,
                expectedComponentIndex);
        Mockito.verify(attachmentService)
                .getAttachmentsDigestValues(ArgumentMatchers.any(ScenarioDto.class),
                        ArgumentMatchers.anyBoolean());
    }

    @Test
    public void shouldPopulateDigestValuesWhenAttachmentServiceResponse() {
        // given
        HashMap<String, String> attachmentServiceResponse = new HashMap<>();
        attachmentServiceResponse.put(DIGEST_KEY, DIGEST_VALUE);
        Mockito.when(attachmentService.getAttachmentsDigestValues(Mockito.any(ScenarioDto.class),
                Mockito.any(Boolean.class))).thenReturn(attachmentServiceResponse);
        // when
        TemplateDataContext actual = sut.prepare(context);

        // then
        final Map<String, Object> additionalValues = actual.getAdditionalValues();
        assertThat(additionalValues.size(), is(greaterThanOrEqualTo(1)));

        assertTrue(additionalValues.containsKey(DIGEST_KEY));
        assertThat((String) additionalValues.get(DIGEST_KEY), is(DIGEST_VALUE));
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
