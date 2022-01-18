package ru.gosuslugi.pgu.sp.adapter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServiceProcessingAdapterService {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceProcessingAdapterService.class)
                .run(args);
    }
}
