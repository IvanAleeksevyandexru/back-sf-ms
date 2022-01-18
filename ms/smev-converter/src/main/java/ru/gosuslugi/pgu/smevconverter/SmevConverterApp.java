package ru.gosuslugi.pgu.smevconverter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SmevConverterApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SmevConverterApp.class)
                .run(args);
    }
}
