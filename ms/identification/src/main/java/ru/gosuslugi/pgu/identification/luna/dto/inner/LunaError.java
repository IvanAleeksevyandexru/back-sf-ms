package ru.gosuslugi.pgu.identification.luna.dto.inner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LunaError {

    @JsonProperty("error_code")
    private Integer errorCode;
    private String desc;
    private String detail;

    public static LunaError getUnknownLunaError() {
        var lunaError = new LunaError();
        lunaError.setErrorCode(-1);
        lunaError.setDesc("Unknown service error");
        lunaError.setDetail("Unknown service error");
        return lunaError;
    }

    public static LunaError getEmptyPhotoLunaError() {
        var lunaError = new LunaError();
        lunaError.setErrorCode(-1);
        lunaError.setDesc("Photo is not recognized");
        lunaError.setDetail("Photo is not recognized");
        return lunaError;
    }

    public static LunaError getNoFaceError() {
        var lunaError = new LunaError();
        lunaError.setErrorCode(-2);
        lunaError.setDesc("No face detected error");
        lunaError.setDetail("No face detected error");
        return lunaError;
    }

    public static LunaError getBadPhotoLunaError() {
        var lunaError = new LunaError();
        lunaError.setErrorCode(-1);
        lunaError.setDesc("Face is not recognized");
        lunaError.setDetail("Face is not recognized");
        return lunaError;
    }

    public static LunaError getSuccess() {
        var lunaError = new LunaError();
        lunaError.setErrorCode(0);
        lunaError.setDesc("success");
        lunaError.setDetail("");
        return lunaError;
    }
}
