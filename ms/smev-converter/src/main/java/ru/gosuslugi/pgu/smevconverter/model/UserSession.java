package ru.gosuslugi.pgu.smevconverter.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
public class UserSession {

    private Long userId;
}
