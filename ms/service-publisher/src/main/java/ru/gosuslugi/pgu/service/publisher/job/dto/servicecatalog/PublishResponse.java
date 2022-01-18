package ru.gosuslugi.pgu.service.publisher.job.dto.servicecatalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishResponse {
    private Integer returnCode;
    private List<String> successScriptList;
    private String failedScript;
    private List<String> incompleteScriptList;
    private List<ValidatingError> validatingErrors;
}
