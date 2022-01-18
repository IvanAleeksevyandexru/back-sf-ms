package ru.gosuslugi.pgu.service;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.gosuslugi.pgu.service.config.ConfigEventListener;

@SpringBootApplication
public class ServiceConfigApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceConfigApplication.class)
                .listeners(new ConfigEventListener())
                .run(args);
    }
}
