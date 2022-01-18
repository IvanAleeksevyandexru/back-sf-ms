package ru.gosuslugi.pgu.pdf.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gosuslugi.pgu.common.core.attachments.AttachmentService;
import ru.gosuslugi.pgu.terrabyte.client.TerrabyteClient;

@Configuration
public class AttachmentConfig {

    @Bean
    public AttachmentService attachmentService(TerrabyteClient terrabyteClient) {
        return new AttachmentService(terrabyteClient);
    }

}
