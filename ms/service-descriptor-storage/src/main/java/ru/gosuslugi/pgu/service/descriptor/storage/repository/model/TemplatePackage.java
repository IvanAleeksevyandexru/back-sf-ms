package ru.gosuslugi.pgu.service.descriptor.storage.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.nio.ByteBuffer;
import java.time.Instant;

@Table("service_template")
@Data
@AllArgsConstructor
public class TemplatePackage {

    @PrimaryKey(value = "service_id", forceQuote = true)
    private String serviceId;

    @Column("updated")
    private Instant updated;

    @Column("package_file")
    @Lazy
    private ByteBuffer packageFile;

    @Column("checksum")
    private String checksum;

}
