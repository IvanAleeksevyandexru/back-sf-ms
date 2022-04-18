package ru.gosuslugi.pgu.service.descriptor.storage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ServiceDescriptorStorageApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceDescriptorStorageApp.class)
                .run(args);
    }
}
