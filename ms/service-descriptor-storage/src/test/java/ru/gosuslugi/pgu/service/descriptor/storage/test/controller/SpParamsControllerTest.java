package ru.gosuslugi.pgu.service.descriptor.storage.test.controller;

import org.assertj.core.matcher.AssertionMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.ServiceDescriptorRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.TemplatePackageRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.DbServiceDescriptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class SpParamsControllerTest {

    private MockMvc mockMvc;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext context;

    @MockBean
    ServiceDescriptorRepository serviceDescriptorRepository;

    @MockBean
    TemplatePackageRepository templatePackageRepository;

    public void init() throws IOException {
        byte[] data = this.getClass().getClassLoader().getResourceAsStream("10000000100.json").readAllBytes();
        String jsonString = new String(data, StandardCharsets.UTF_8);
        DbServiceDescriptor dbServiceDescriptor = new DbServiceDescriptor("10000000100", Instant.now(), jsonString);
        Optional<DbServiceDescriptor> dbServiceDescriptorOptional = Optional.of(dbServiceDescriptor);
        when(serviceDescriptorRepository.findById("10000000100")).thenReturn(dbServiceDescriptorOptional);

        data = this.getClass().getClassLoader().getResourceAsStream("10000000101.json").readAllBytes();
        jsonString = new String(data, StandardCharsets.UTF_8);
        dbServiceDescriptor = new DbServiceDescriptor("10000000101", Instant.now(), jsonString);
        dbServiceDescriptorOptional = Optional.of(dbServiceDescriptor);
        when(serviceDescriptorRepository.findById("10000000101")).thenReturn(dbServiceDescriptorOptional);
    }

    @Before
    public void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        init();
    }

    @Test
    public void getSpParams() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/sp/params/{serviceId}", "10000000100")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(getDocument("tmp/sp/params"))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string(new AssertionMatcher<String>() {
                            @Override
                            public void assertion(String result) throws AssertionError {
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    assert jsonObject.length() == 2;
                                    assert "Param1Value".equals(jsonObject.get("param1"));
                                    assert "Param2Value".equals(jsonObject.get("param2"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }));
    }

    @Test
    public void getSpNoParams() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/sp/params/{serviceId}", "10000000101")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(getDocument("tmp/sp/noparams"))
                        .andExpect(status().is(HttpStatus.OK.value()))
                        .andExpect(MockMvcResultMatchers.content().string(new AssertionMatcher<String>() {
                            @Override
                            public void assertion(String result) throws AssertionError {
                                assert "".equals(result);
                            }
                        }));
    }

    @Test
    public void getSpNoService() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/sp/params/{serviceId}", "10000000102")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(getDocument("tmp/sp/noservice"))
                        .andExpect(status().is(HttpStatus.OK.value()))
                        .andExpect(MockMvcResultMatchers.content().string(new AssertionMatcher<String>() {
                            @Override
                            public void assertion(String result) throws AssertionError {
                                assert "".equals(result);
                            }
                        }));
    }

    private RestDocumentationResultHandler getDocument(String docPath) {
        return document(docPath,
                pathParameters(
                        parameterWithName("serviceId").description("ID сервиса")
                ));
    }

}
