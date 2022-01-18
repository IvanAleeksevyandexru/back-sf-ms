package ru.gosuslugi.pgu.sp.adapter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * Service for deb&debug mode
 */
@RequiredArgsConstructor
@ConditionalOnExpression("${dev-mode.enabled}")
@Service
public class DevTemplateService {
}
