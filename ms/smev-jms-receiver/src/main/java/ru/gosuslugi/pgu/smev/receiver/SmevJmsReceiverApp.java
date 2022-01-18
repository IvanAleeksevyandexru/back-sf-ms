package ru.gosuslugi.pgu.smev.receiver;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class SmevJmsReceiverApp {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SmevJmsReceiverApp.class)
                .run(args);
    }
}
