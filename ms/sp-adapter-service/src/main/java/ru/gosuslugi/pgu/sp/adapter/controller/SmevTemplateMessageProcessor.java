package ru.gosuslugi.pgu.sp.adapter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.gosuslugi.pgu.dto.SpAdapterDto;
import ru.gosuslugi.pgu.dto.SpRequestErrorDto;
import ru.gosuslugi.pgu.dto.SpResponseOkDto;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpAdapterServiceException;
import ru.gosuslugi.pgu.sp.adapter.exceptions.SpRequestException;
import ru.gosuslugi.pgu.sp.adapter.config.props.SpKafkaProducersProperties;
import ru.gosuslugi.pgu.sp.adapter.service.SmevService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmevTemplateMessageProcessor {

    private final SmevService smevTemplateEngineService;
    private final KafkaTemplate<Long, SpRequestErrorDto> errorTopicTemplate;
    private final KafkaTemplate<Long, SpResponseOkDto> spResponseTopicTemplate;
    private final SpKafkaProducersProperties spKafkaProducersProperties;

    public void processMessage(SpAdapterDto spAdapterDto) {
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
            spResponseTopicTemplate.send(spKafkaProducersProperties.getResponse().getTopic(), spAdapterDto.getOrderId(), responseOkDto);

        } catch (SpRequestException ex) {
            log.error(String.format("Произошла ошибка отправки заявления в SP %s", spAdapterDto), ex);
            val spError = ex.getSpRequestError();
            spError.setAdapterRequestDto(spAdapterDto);
            errorTopicTemplate.send(spKafkaProducersProperties.getErrors().getTopic(), spAdapterDto.getOrderId(), spError);
        } catch (Exception ex) {
            log.error(String.format("Произошла ошибка формирования заявления %s", spAdapterDto.toString()), ex);
            val spError = new SpRequestErrorDto();
            spError.setAdapterRequestDto(spAdapterDto);
            spError.setCause(ex.getMessage());
            errorTopicTemplate.send(spKafkaProducersProperties.getSelfErrors().getTopic(), spAdapterDto.getOrderId(), spError);
        }
    }

}
