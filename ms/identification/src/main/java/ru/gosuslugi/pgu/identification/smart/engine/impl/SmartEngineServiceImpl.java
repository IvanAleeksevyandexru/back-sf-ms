package ru.gosuslugi.pgu.identification.smart.engine.impl;

import com.smartengines.common.Image;
import com.smartengines.id.IdEngine;
import com.smartengines.id.IdResult;
import com.smartengines.id.IdSession;
import com.smartengines.id.IdSessionSettings;
import com.smartengines.id.IdTextField;
import com.smartengines.id.IdTextFieldsMapIterator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.identification.smart.engine.SmartEngineProperties;
import ru.gosuslugi.pgu.identification.smart.engine.SmartEngineService;
import ru.gosuslugi.pgu.identification.smart.engine.dto.RecognizeResponse;
import ru.gosuslugi.pgu.identification.smart.engine.dto.inner.FieldData;
import ru.gosuslugi.pgu.identification.smart.engine.model.SmartEngineAggregate;
import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;
import ru.gosuslugi.pgu.terrabyte.client.model.FileInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
@Slf4j

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SmartEngineProperties.class)
@ConditionalOnProperty(value = "smart-engine.enabled", havingValue = "true")
public class SmartEngineServiceImpl implements SmartEngineService {

    private final TerrabyteClient terrabyteClient;
    private final SmartEngineProperties props;

    private IdEngine engine;

    @PostConstruct
    public void init(){
        System.loadLibrary("jniidengine");
        engine = IdEngine.Create(props.getConfigPath(), true);
    }

    @PreDestroy
    public void destroy(){
        engine.delete();
    }

    @Override
    public RecognizeResponse getPassportData(FileInfo fileInfo){

        /**
         * Пролятый дух Имярека
         * Мы победили тебя!
         */
        var recognizeResponse = new RecognizeResponse();
        val aggregate = new SmartEngineAggregate(
                Image.FromFileBuffer(terrabyteClient.getFile(fileInfo.getFileUid())),
                engine,
                props.getToken(),
                props.getDocumentType()
        );
        aggregate.init();
        recognizeResponse.setFields(aggregate.getTextFields());
        recognizeResponse.setPersonPhoto(aggregate.getPhoto());
        aggregate.destroy();
        return recognizeResponse;
    }
}
