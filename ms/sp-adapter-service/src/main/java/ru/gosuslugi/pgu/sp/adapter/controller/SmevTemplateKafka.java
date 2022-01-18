package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.common.kafka.properties.KafkaConsumerProperties;
import ru.gosuslugi.pgu.common.kafka.service.BatchKafkaMessageProcessing;
import ru.gosuslugi.pgu.common.logging.service.SpanService;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;
import ru.gosuslugi.pgu.dto.SpResponseOkDto;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterServiceException;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpRequestException;
import ru.gosuslugi.pgu.sp.adapter.service.SmevService;

import java.util.List;

/**
 * Обмен сообщениями с сервисом форм
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka.form-service", name = "enabled", havingValue = "true")
public class SmevTemplateKafka extends BatchKafkaMessageProcessing<SpAdapterDto> {

    private final SmevService smevTemplateEngineService;
    private final KafkaTemplate<Long, SpRequestErrorDto> errorTopicTemplate;
    private final KafkaTemplate<Long, SpResponseOkDto> spResponseTopicTemplate;

    @Getter
    protected final KafkaConsumerProperties kafkaFormServiceProperties;
    @Getter
    protected final SpanService spanService;

    /**
     * Запуск процесса генерации файлов и вызова SP
     */
    @KafkaListener(
            topics = "#{kafkaFormServiceProperties.topicName}",
            groupId = "#{kafkaFormServiceProperties.groupId}",
            concurrency = "#{kafkaFormServiceProperties.concurrency}",
            containerFactory = "smevRequestKafkaListenerContainerFactory"
    )
    public void sendSmevRequest(List<Message<SpAdapterDto>> messages) {
        log.info("Принято из kafka очереди {} {} сообщений", kafkaFormServiceProperties.getTopicName(), messages.size());
        process(messages);
    }

    @Override
    protected void processMessage(SpAdapterDto spAdapterDto) {
        log.info("Обработка сообщения SpAdapterDto из очереди: {}", spAdapterDto);
        try {
            var result = true;
            if (spAdapterDto.isSigned()) {
                result = smevTemplateEngineService.processSignedSmevRequest(
                        spAdapterDto.getServiceId(),
                        spAdapterDto.getOrderId(),
                        spAdapterDto.getOid(),
                        spAdapterDto.getRole(),
                        spAdapterDto.getOrgId(),
                        true
                );
            } else {
                result = smevTemplateEngineService.processSmevRequest(
                        spAdapterDto.getServiceId(),
                        spAdapterDto.getOrderId(),
                        spAdapterDto.getOid(),
                        spAdapterDto.getRole(),
                        spAdapterDto.getOrgId(),
                        true
                );
            }
            if (!result) {
                log.error("Результат запроса отрицательный, причина не понятна... как-то так.\n {}", spAdapterDto);
                throw new SpAdapterServiceException("Результат запроса отрицательный, причина не понятна... как-то так");
            }
            SpResponseOkDto responseOkDto = new SpResponseOkDto(spAdapterDto.getOrderId(), spAdapterDto.getOid(), spAdapterDto.getServiceId(), spAdapterDto.getTargetId());
            spResponseTopicTemplate.send(kafkaFormServiceProperties.getResponseTopicName(), spAdapterDto.getOrderId(), responseOkDto);

        } catch (SpRequestException ex) {
            log.error(String.format("Произошла ошибка отправки заявления в SP %s", spAdapterDto), ex);
            val spError = ex.getSpRequestError();
            spError.setAdapterRequestDto(spAdapterDto);
            errorTopicTemplate.send(kafkaFormServiceProperties.getErrorsTopicName(), spAdapterDto.getOrderId(), spError);
        } catch (Exception ex) {
            log.error(String.format("Произошла ошибка формирования заявления %s", spAdapterDto.toString()), ex);
            val spError = new SpRequestErrorDto();
            spError.setAdapterRequestDto(spAdapterDto);
            spError.setCause(ex.getMessage());
            errorTopicTemplate.send(kafkaFormServiceProperties.getSelfErrorsTopicName(), spAdapterDto.getOrderId(), spError);
        }
    }
}
