package ru.gosuslugi.pgu.voskhod.adapter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class VoskhodAdapterApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(VoskhodAdapterApp.class)
                .run(args);
    }
}
