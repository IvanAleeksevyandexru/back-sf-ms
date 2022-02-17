package ru.gosuslugi.pgu.test.model;

import ru.gosuslugi.pgu.draft.model.DraftHolderDto;
import ru.gosuslugi.pgu.dto.ApplicantRole;
import ru.gosuslugi.pgu.sp.adapter.data.TemplatesDataContext;

import java.io.IOException;
import java.net.URL;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

public class SpAdapterTestData {
    public static final String SERVICE_ID = "1";
    public static final Long OID = 2L;
    public static final Long ORDER_ID = 3L;
    public static final String ROLE_ID = ApplicantRole.Applicant.name();
    public static final Long ORG_ID = 4L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Getter
    private final DraftHolderDto draft;
    @Getter
    @Setter
    private boolean skip17Status;
    private TemplatesDataContext dataContext;

    /**
     * @param draftJsonPath путь до JSON-файла с черновиком ({@link DraftHolderDto}).
     */
    public SpAdapterTestData(final String draftJsonPath) {
        draft = readAndDeserialize(draftJsonPath, DraftHolderDto.class);
        dataContext = new TemplatesDataContext();
        dataContext.setOid(OID);
        dataContext.setOrderId(ORDER_ID);
    }

    public TemplatesDataContext getTemplateDataContext() {
        return dataContext;
    }

    @SneakyThrows
    public <T> T readAndDeserialize(String fileName, Class<T> targetClass) {
        final URL resource = getTestResource(fileName);
        return OBJECT_MAPPER.readValue(resource, targetClass);
    }

    @SneakyThrows
    public <T> T readAndDeserialize(String fileName, TypeReference<? extends T> typeRef) {
        final URL resource = getTestResource(fileName);
        return OBJECT_MAPPER.readValue(resource, typeRef);
    }

    public URL getTestResource(String fileName) {
        return getClass().getClassLoader().getResource(fileName);
    }
}
