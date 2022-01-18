package ru.gosuslugi.pgu.rename;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

// TODO KK Переименовать класс
@SpringBootApplication
public class RENAMEApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RENAMEApp.class)
                .run(args);
    }
}
