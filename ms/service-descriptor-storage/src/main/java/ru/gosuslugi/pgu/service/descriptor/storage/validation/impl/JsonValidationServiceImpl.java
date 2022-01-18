package ru.gosuslugi.pgu.service.descriptor.storage.validation.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.service.descriptor.storage.exception.ServiceDescriptorValidationException;
import ru.gosuslugi.pgu.service.descriptor.storage.validation.JsonValidationService;
import ru.gosuslugi.pgu.service.descriptor.storage.validation.exception.SpConfigValidationException;

import java.io.IOException;

@Slf4j
@Service
public class JsonValidationServiceImpl implements JsonValidationService {

    @Value("${config.spConfig.jsonSchema:/SPConfig_schema.json}")
    private String jsonConfigSchemaName;

    private final ObjectMapper objectMapper;

    private final JsonSchema spValidationSchema;

    public JsonValidationServiceImpl(ObjectMapper objectMapper) {
        this.spValidationSchema = getSpJsonSchema();
        this.objectMapper = objectMapper;
    }

    private JsonSchema getSpJsonSchema() {
        if (jsonConfigSchemaName == null) {
            log.warn("Не определен параметр config.spConfig.jsonSchema, указывающий расположение JSON схемы для валидации");
            return null;
        }

        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        ObjectMapper mapper = new ObjectMapper();
        JsonSchema jsonSchema = null;
        try {
            jsonSchema = factory.getJsonSchema(mapper.readTree(JsonValidationServiceImpl.class.getResourceAsStream(jsonConfigSchemaName)));
            log.info("Получена схема для валидации JSON из файла " + jsonConfigSchemaName);
        } catch (ProcessingException | IOException e) {
            log.warn("Ошибка получения схемы для валидации JSON из файла " + jsonConfigSchemaName + ": " + e);
        }
        return jsonSchema;
    }

    @Override
    public void validate(String string, Class<?> clazz) throws ServiceDescriptorValidationException {
        if (spValidationSchema == null) {
            log.warn("JSON схема для валидации не определена.");
            return;
        }

        JsonNode node = null;
        try {
            node = objectMapper.readTree(string);
        } catch (JsonProcessingException e) {
            throw new ServiceDescriptorValidationException("Incorrect structure of json. Details: " + e, e);
        }
        try {
            objectMapper.readValue(string, clazz);
        } catch (JsonProcessingException e) {
            throw new ServiceDescriptorValidationException("Error while processing object from json. Details: " + e + " for object class: " + clazz, e);
        }
        try {
            validateSpConfigsBySchema(node);
        } catch (ProcessingException | SpConfigValidationException e){
            throw new ServiceDescriptorValidationException("SP config validation exception: " + e.getMessage(), e);
        }
    }

    /**
     * Validation of SP configs by schema
     * @param node serviceJson node to validate
     * @throws SpConfigValidationException exception in validation
     */
    private void validateSpConfigsBySchema(JsonNode node) throws ProcessingException, SpConfigValidationException {
        ProcessingReport report = spValidationSchema.validate(node);
        StringBuilder sb = new StringBuilder();
        for(ProcessingMessage message : report){
            sb.append(message.getMessage()).append("\n");
        }
        String message = sb.toString();
        if(!StringUtils.isEmpty(message)){
            throw new SpConfigValidationException(message);
        }
    }

}
