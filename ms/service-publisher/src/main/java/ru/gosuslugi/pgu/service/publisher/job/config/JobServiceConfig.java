package ru.gosuslugi.pgu.service.publisher.job.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.gosuslugi.pgu.service.publisher.job.JobProperties;

@Configuration
@EnableJpaRepositories(basePackages = "ru.gosuslugi.pgu.service.publisher.job.repository")
@EnableScheduling
@EnableConfigurationProperties(JobProperties.class)
public class JobServiceConfig {
}
