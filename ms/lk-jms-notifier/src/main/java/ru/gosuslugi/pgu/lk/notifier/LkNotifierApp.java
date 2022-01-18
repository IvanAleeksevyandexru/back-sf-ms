package ru.gosuslugi.pgu.lk.notifier;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LkNotifierApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LkNotifierApp.class)
                .run(args);
    }
}
