package ru.gosuslugi.pgu.service.publisher.vcs.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gosuslugi.pgu.common.core.exception.PguException;
import ru.gosuslugi.pgu.service.publisher.vcs.VcsProperties;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(VcsProperties.class)
public class VcsConfig {

    @Bean
    public Git gitRepo(VcsProperties vcsProperties, CredentialsProvider credentialsProvider) {
        var localRepoPath = Path.of(vcsProperties.getLocalRepoPath());
        try {
            if (localRepoPath.toFile().exists()) {
                return Git.open(localRepoPath.toFile());
            }

            return Git.cloneRepository()
                    .setURI(vcsProperties.getRepoUrl())
                    .setCredentialsProvider(credentialsProvider)
                    .setDirectory(localRepoPath.toFile())
                    .call();
        } catch (GitAPIException | IOException e) {
            log.error("Ошибка инициализации репозитория", e);
        }
        throw new PguException("Ошибка инициализации репозитория");
    }

    @Bean
    public Map<String, String> servicesFromConfig(VcsProperties vcsProperties) {
        return vcsProperties.getServices();
    }

    @Bean
    public CredentialsProvider credentialsProvider(VcsProperties vcsProperties) {
        return new UsernamePasswordCredentialsProvider(vcsProperties.getUserName(), vcsProperties.getUserPass());
    }

}
