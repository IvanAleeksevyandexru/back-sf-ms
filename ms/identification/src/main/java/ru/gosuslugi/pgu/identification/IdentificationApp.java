package ru.gosuslugi.pgu.identification;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class IdentificationApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(IdentificationApp.class)
                .run(args);
    }
}
