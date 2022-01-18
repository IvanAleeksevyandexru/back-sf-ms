package ru.gosuslugi.pgu.pdf.template;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PdfTemplateGeneratorApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PdfTemplateGeneratorApp.class)
                .run(args);
    }
}
