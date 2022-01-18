package ru.gosuslugi.pgu.service.descriptor.storage.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "ru.gosuslugi.pgu.service.descriptor.storage.repository")
public class CassandraConfiguration {

}
