package ru.gosuslugi.pgu.smev.receiver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jms")
public class CustomJmsProperties {

    private String connection;

    private String username;

    private String password;

    private long clientFailureCheckPeriod = 30000;

    private long connectionTTL = 60000;

    private long retryInterval = 500;

    private double retryIntervalMultiplier = 1;

    private long maxRetryInterval = 2000;

    private int reconnectAttempts = 0;

}
