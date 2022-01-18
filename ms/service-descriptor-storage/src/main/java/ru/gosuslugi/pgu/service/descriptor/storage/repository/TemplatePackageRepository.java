package ru.gosuslugi.pgu.service.descriptor.storage.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import ru.gosuslugi.pgu.service.descriptor.storage.repository.model.TemplatePackage;

public interface TemplatePackageRepository extends CassandraRepository<TemplatePackage, String> {

}
