package ru.gosuslugi.pgu.service.publisher;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication(exclude = {
//        MultipartAutoConfiguration.class,
        TaskExecutionAutoConfiguration.class,
        TaskSchedulingAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class})
public class ServicePublisherApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServicePublisherApp.class)
                .run(args);
    }
}
