package ru.gosuslugi.pgu.service.descriptor.storage.test.controller;

import org.apache.commons.codec.binary.Base64;
import org.assertj.core.matcher.AssertionMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.cassandra.core.CassandraTemplate;
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
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.TemplatePackage;
import ru.gosuslugi.pgu.service.descriptor.storage.service.FindComponentRegistryService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class SingleTemlateControllerRestdocsTest {

    private MockMvc mockMvc;

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext context;

    @MockBean
    ServiceDescriptorRepository serviceDescriptorRepository;

    @MockBean
    TemplatePackageRepository templatePackageRepository;

    @MockBean
    CassandraTemplate cassandraTemplate;

    public void init() throws IOException {
        byte[] data = this.getClass().getClassLoader().getResourceAsStream("package_10000000100.zip").readAllBytes();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        TemplatePackage initialTemplatePackage = new TemplatePackage("10000000100", Instant.now(), buffer, "");
        Optional<TemplatePackage> initialTemplatePackageOptional = Optional.of(initialTemplatePackage);
        when(templatePackageRepository.findById("10000000100")).thenReturn(initialTemplatePackageOptional);
        data = this.getClass().getClassLoader().getResourceAsStream("WrongArchive.zip").readAllBytes();
        buffer = ByteBuffer.wrap(data);
        initialTemplatePackage = new TemplatePackage("10000000101", Instant.now(), buffer, "");
        initialTemplatePackageOptional = Optional.of(initialTemplatePackage);
        when(templatePackageRepository.findById("10000000101")).thenReturn(initialTemplatePackageOptional);
    }

    @Before
    public void setUp() throws IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
        init();
    }

    @Test
    public void getSingleTemplate() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/v1/singletemplates/{serviceId}?path=sections/applicant/pdf_10000000100_Applicant_additional_info.vm", "10000000100")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andDo(getDocument("tmp/singletemplatescrc/get"))
                .andExpect(MockMvcResultMatchers.content().string(new AssertionMatcher<String>() {
                    @Override
                    public void assertion(String result) throws AssertionError {
                        try {
                            byte[] data = this.getClass().getClassLoader().getResourceAsStream("pdf_10000000100_Applicant_additional_info.vm").readAllBytes();
                            byte[] encodedResult = Base64.decodeBase64(result);
                            assert new String(data, StandardCharsets.UTF_8).replace("\r\n", "\n")
                                    .equals(new String(encodedResult, StandardCharsets.UTF_8).replace("\r\n", "\n"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }));
    }

    @Test
    public void getWrongPathSingleTemplate() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/v1/singletemplates/{serviceId}?path=wrong_path", "10000000100")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(getDocument("tmp/singletemplatescrc/wrongpath"))
                        .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void getWrongArchiveSingleTemplate() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/v1/singletemplates/{serviceId}?path=wrong_path", "10000000101")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(getDocument("tmp/singletemplatescrc/wrongarchive"))
                        .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void getSingleTemplateCRC() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/v1/singletemplates/{serviceId}/crc?" +
                        "path=sections/applicant/pdf_10000000100_Applicant_additional_info.vm", "10000000100")
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andDo(getDocument("tmp/singletemplatescrc/get"))
                        .andExpect(MockMvcResultMatchers.content().string(new AssertionMatcher<String>() {
                            @Override
                            public void assertion(String result) throws AssertionError {
                                assert "3547713431".equals(result);
                            }
                        }));
    }

    @Test
    public void getWrongPathSingleTemplateCRC() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/v1/singletemplates/{serviceId}/crc?path=wrong_path", "10000000100")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(getDocument("tmp/singletemplatescrc/wrongpath"))
                        .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void getWrongArchiveSingleTemplateCRC() throws Exception {
        ResultActions result =
                this.mockMvc.perform(get("/v1/singletemplates/{serviceId}/crc?path=dont_matter", "10000000101")
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(getDocument("tmp/singletemplatescrc/wrongarchive"))
                        .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    private RestDocumentationResultHandler getDocument(String docPath){
        return document(docPath,
                requestParameters(
                        parameterWithName("path").description("Путь до шаблона внутри архива сервиса")
                ),
                pathParameters(
                        parameterWithName("serviceId").description("ID сервиса")
                ));
    }

}
