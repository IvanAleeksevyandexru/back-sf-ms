package ru.gosuslugi.pgu.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ConfigEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private final String separator = System.lineSeparator();

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        printActiveProperties(event.getEnvironment());
    }

    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        log.info("Stopping application...");
    }

    private void printActiveProperties(ConfigurableEnvironment env) {

        List<MapPropertySource> propertySources = new ArrayList<>();

        env.getPropertySources().forEach(it -> {
            if (it instanceof MapPropertySource && it.getName().contains("applicationConfig")) {
                propertySources.add((MapPropertySource) it);
            }
        });

        String properties = propertySources.stream()
                .map(propertySource -> propertySource.getSource().keySet())
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .map(key -> key + "=" + env.getProperty(key))
                .collect(Collectors.joining(separator));
        log.info("************************* Current configuration properties ******************************" + separator
                + properties + separator
                + "*****************************************************************************************");
    }

}