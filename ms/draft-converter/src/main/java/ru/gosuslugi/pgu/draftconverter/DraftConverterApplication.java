package ru.gosuslugi.pgu.draftconverter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Основной класс микросервиса.
 */
@SpringBootApplication
public class DraftConverterApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DraftConverterApplication.class).run(args);
    }
}
