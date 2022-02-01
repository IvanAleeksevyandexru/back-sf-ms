package ru.gosuslugi.pgu.sp.adapter.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.gosuslugi.pgu.common.kafka.service.KafkaBatchProcessorConfig;

@Configuration
@Import(KafkaBatchProcessorConfig.class)
public class SmevTemplateKafkaConfiguration {
}
