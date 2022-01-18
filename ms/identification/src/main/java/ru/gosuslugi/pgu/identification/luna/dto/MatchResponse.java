package ru.gosuslugi.pgu.identification.luna.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import ru.gosuslugi.pgu.identification.luna.dto.inner.DetectionQuality;
import ru.gosuslugi.pgu.identification.luna.dto.inner.LunaError;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchResponse {

    private MatchReference reference;
    private List<Match> matches;
    private String selfieFaceId;
    private String faceId;
    private DetectionQuality quality;
    private LunaError error;
    private Integer score;

    public Double getSimilarity() {
        if (!CollectionUtils.isEmpty(matches)
                && !CollectionUtils.isEmpty(matches.get(0).getResult()))
            return matches.get(0).getResult().get(0).getSimilarity();
        return Double.MIN_VALUE;
    }

    @Data
    public static class Match {
        private List<MatchResult> result;
        private Map<String, Object> filters;
    }

    @Data
    public static class MatchResult {
        private MatchResultFace face;
        private Double similarity;
    }

    @Data
    public static class MatchResultFace {
        private String face_id;
        private String account_id;
        private String event_id;
        private String user_data;
        private String create_time;
        private String external_id;
        private String avatar;
        private String[] lists;
    }

    @Data
    public static class MatchReference {
        private String id;
        private String type;
    }

}
