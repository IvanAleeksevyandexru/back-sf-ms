package ru.gosuslugi.pgu.service.publisher.job.dto.servicecatalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidatingError {
    private String validatingErrorDesc;
    private String validatingField;
}
