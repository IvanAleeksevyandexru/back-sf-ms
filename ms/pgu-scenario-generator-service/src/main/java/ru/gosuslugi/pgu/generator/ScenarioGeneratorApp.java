package ru.gosuslugi.pgu.generator;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ScenarioGeneratorApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ScenarioGeneratorApp.class)
                .run(args);
    }
}
