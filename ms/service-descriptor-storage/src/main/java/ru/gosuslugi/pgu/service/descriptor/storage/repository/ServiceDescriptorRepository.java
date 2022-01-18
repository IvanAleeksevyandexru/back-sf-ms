package ru.gosuslugi.pgu.service.descriptor.storage.repository;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.data.cassandra.repository.CassandraRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.DbServiceDescriptor;

import java.util.Optional;

public interface ServiceDescriptorRepository extends CassandraRepository<DbServiceDescriptor, String> {

    @Override
    @Timed(value = "cassandra.repo", percentiles = {0.99, 0.97, 0.75})
    @NewSpan("cassandra.query.findById")
    Optional<DbServiceDescriptor> findById(String id);

    @Override
    @Timed(value = "cassandra.repo", percentiles = {0.99, 0.97, 0.75})
    @NewSpan("cassandra.query.save")
    <S extends DbServiceDescriptor> S save(S s);
}
