package ru.gosuslugi.pgu.service.descriptor.storage.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("service_descriptor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbServiceDescriptor {

    @Id
//    Не работает с @Id
//    @Column("service_id")
    private String serviceId;

    @Column("updated")
    private Instant updated;

    @Column("body")
    private String body;
}
