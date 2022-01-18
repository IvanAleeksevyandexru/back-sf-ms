package ru.gosuslugi.pgu.xmlservice;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Стартовый класс сервиса.
 */
@SpringBootApplication
public class XmlServiceApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(XmlServiceApp.class)
                .run(args);
    }
}
