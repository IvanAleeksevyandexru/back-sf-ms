package ru.gosuslugi.pgu.identification.luna.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class MatchRequest {

    private List<Candidate> candidates = new ArrayList<>();
    private List<Map<String, String>> references = new ArrayList<>();

    public MatchRequest(String passportFaceId, String selfieFaceId) {
        Candidate candidate = new Candidate();
        candidate.getFilters().faceIds[0] = passportFaceId;
        candidates.add(candidate);
        references.add(Map.of(
                "type", "face",
                "id", selfieFaceId
        ));
    }

    @Data
    public static class Candidate {
        private CandidateFilter filters = new CandidateFilter();
    }

    @Data
    public static class CandidateFilter {

        private String origin = "faces";

        @JsonProperty("face_ids")
        private String[] faceIds = {""};

    }


}
