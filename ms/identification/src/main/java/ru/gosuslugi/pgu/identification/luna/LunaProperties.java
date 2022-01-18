package ru.gosuslugi.pgu.identification.luna;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@Data
@ConfigurationProperties(prefix = "luna")
public class LunaProperties {

    private String handlerId;
    private String accountId;
    private String verifierId;
    private String apiUrl;

    private String createFacePath;
    private String matchFacesPath;
    private String matchVideoPath;

    public String getCreateFullPath(){
        if (StringUtils.isEmpty(createFacePath))
            createFacePath = apiUrl + "/6/handlers/" + handlerId + "/events?external_id={guid}";
        return createFacePath;
    }

    public String getMatchFacesFullPath(){
        if (StringUtils.isEmpty(matchFacesPath))
            matchFacesPath = apiUrl + "/6/matcher/faces";
        return matchFacesPath;
    }

    public String getMatchVideoFullPath(){
        if (StringUtils.isEmpty(matchVideoPath))
            matchVideoPath = apiUrl + "/6/verifiers/" + verifierId + "/verifications?face_ids={passportId},{selfieId}";
        return matchVideoPath;
    }
}
