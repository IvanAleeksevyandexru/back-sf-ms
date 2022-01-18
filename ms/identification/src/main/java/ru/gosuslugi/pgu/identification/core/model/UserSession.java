package ru.gosuslugi.pgu.identification.core.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
public class UserSession {

    private Long userId;

    private String cookie;
}
