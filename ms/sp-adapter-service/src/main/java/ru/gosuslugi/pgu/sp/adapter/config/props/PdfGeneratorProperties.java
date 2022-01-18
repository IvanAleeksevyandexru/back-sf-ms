package ru.gosuslugi.pgu.sp.adapter.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "pdf-generator")
public class PdfGeneratorProperties {

    private Boolean enabled = false;

    private String url;
}
