package ru.gosuslugi.pgu.identification.smart.engine.model;

import com.smartengines.common.Image;
import com.smartengines.id.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.gosuslugi.pgu.identification.smart.engine.dto.inner.FieldData;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
public class SmartEngineAggregate{
    private Image image;
    private IdSession session;
    private IdSessionSettings settings;
    private IdResult fullData;

    public SmartEngineAggregate(Image image,IdEngine engine ,String token, String documentType) {
        this.image = image;
        this.settings = engine.CreateSessionSettings();
        settings.AddEnabledDocumentTypes(documentType);
        this.session = engine.SpawnSession(settings, token);
    }

    public void destroy(){
        fullData.delete();
        image.delete();
        session.delete();
        settings.delete();
    }

    public void init(){
        this.fullData = session.Process(image);

    }

    public Map<String, FieldData> getTextFields() {
        Map<String, FieldData> fields = new LinkedHashMap<>();
        for (IdTextFieldsMapIterator it = fullData.TextFieldsBegin(); !it.Equals(fullData.TextFieldsEnd()); it.Advance()) {
            IdTextField field = it.GetValue();
            var data = new FieldData(field.GetValue().GetFirstString().GetCStr(), field.GetBaseFieldInfo().GetConfidence());
            fields.put(field.GetName(), data);
        }
        return fields;
    }

    public byte[] getPhoto() {
        try {
            var passportPhoto = fullData.GetImageField("photo");
            if (Objects.nonNull(passportPhoto))
                return Base64.getDecoder().decode(passportPhoto.GetValue().GetBase64String().GetCStr().getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex){
            log.error("Smart engine error during process document photo", ex);
        }
        return new byte[0];
    }

}
