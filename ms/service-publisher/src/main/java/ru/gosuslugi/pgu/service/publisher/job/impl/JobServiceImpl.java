package ru.gosuslugi.pgu.service.publisher.job.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.gosuslugi.pgu.common.core.json.JsonProcessingUtil;
import ru.gosuslugi.pgu.service.publisher.job.JobProperties;
import ru.gosuslugi.pgu.service.publisher.job.JobService;
import ru.gosuslugi.pgu.service.publisher.job.client.ServiceCatalogRestClient;
import ru.gosuslugi.pgu.service.publisher.job.dto.TaskCreateDto;
import ru.gosuslugi.pgu.service.publisher.job.dto.TaskInfoDto;
import ru.gosuslugi.pgu.service.publisher.job.dto.servicecatalog.PublishResponse;
import ru.gosuslugi.pgu.service.publisher.job.mapper.TaskMapper;
import ru.gosuslugi.pgu.service.publisher.job.model.Task;
import ru.gosuslugi.pgu.service.publisher.job.model.TaskStatus;
import ru.gosuslugi.pgu.service.publisher.job.repository.TaskRepository;
import ru.gosuslugi.pgu.service.publisher.vcs.VcsService;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final TaskRepository repository;
    private final RestTemplate restTemplate;
    private final JobProperties properties;
    private final VcsService vcsService;
    private final TaskMapper taskMapper;
    private final ServiceCatalogRestClient serviceCatalogRestClient;

    @Override
    public TaskInfoDto createTask(TaskCreateDto taskCreateDto) {
        return saveTaskInfoDto(taskCreateDto, null);
    }

    @SneakyThrows
    private TaskInfoDto saveTaskInfoDto(TaskCreateDto taskCreateDto, Long parentTaskId) {
        Task newTask = taskMapper.toTask(taskCreateDto);
        newTask.setParentTaskId(parentTaskId);
        newTask.setCreated(LocalDateTime.now());
        newTask.setStatus(TaskStatus.CREATED);

        if (taskCreateDto.getSendDescriptor()) {
            newTask.setDescriptor(vcsService.getDescriptor(taskCreateDto.getServiceVersion(), taskCreateDto.getServiceId()));
        }
        if (taskCreateDto.getSendTemplates()) {
            File zip = vcsService.getTemplates(taskCreateDto.getServiceVersion(), taskCreateDto.getServiceId());
            newTask.setTemplates(Files.readAllBytes(zip.toPath()));
        }
        if (taskCreateDto.getSendConfig()) {
            newTask.setConfig(vcsService.getConfig(taskCreateDto.getServiceVersion(), taskCreateDto.getServiceId()));
        }
        var savedTask = repository.save(newTask);
        return taskMapper.toInfoDto(savedTask);
    }

    @Override
    @Scheduled(fixedRateString = "${job-service.schedule-rate-milliseconds}")
    public void processTask() {
        LocalDateTime minDatetime = LocalDateTime.now().minusMinutes(properties.getTaskSelectMinutesCount());
        var taskBox = repository.findFirstByCreatedGreaterThanAndStatus(minDatetime, TaskStatus.CREATED);
        if (taskBox.isEmpty()) return;

        Task task = taskBox.get();
        task.setStatus(TaskStatus.IN_PROGRESS);
        repository.save(task);
        boolean isSendDescriptor = sendDescriptor(task);
        boolean isSendTemplates = sendTemplates(task);
        boolean isSendConfig = sendConfig(task);

        boolean isFullProcessed = isSendDescriptor && isSendTemplates && isSendConfig;

        TaskStatus taskStatus = isFullProcessed ? TaskStatus.PROCESSED : TaskStatus.FAILED;
        task.setStatus(taskStatus);
        if (!isFullProcessed && task.getRevertOnFail()) {
                task.setStatus(TaskStatus.FAILED_AND_REVERT);
                createRevertTask(task);
        }
        repository.save(task);
        log.info("task processed: {}", taskBox.get().getId());
    }

    @Override
    public List<TaskInfoDto> getTasks() {
        return repository.findAll().stream().map(taskMapper::toInfoDto).collect(Collectors.toList());
    }

    @Override
    public Map<String, JobProperties.Environment> getEnvironments() {
        return properties.getEnvironments();
    }

    private void createRevertTask(Task parentTask) {
        TaskCreateDto revertTask = taskMapper.toDto(parentTask);
        revertTask.setRevertOnFail(false);
        revertTask.setRevertToVersion(null);
        saveTaskInfoDto(revertTask, parentTask.getId());
    }

    private boolean sendDescriptor(@NotNull Task task) {
        if (!task.getSendDescriptor()) return true;
        if (Objects.isNull(task.getDescriptor())) return false;
        try {
            JobProperties.Environment environment = properties.getEnvironments().get(task.getTargetEnvironment());
            if (Objects.isNull(environment) || StringUtils.isEmpty(environment.getTemplatesUrl()))
                return false;

            byte[] descriptor = task.getDescriptor();
            HttpEntity body = new HttpEntity(descriptor, getHeaders());
            var response = restTemplate.exchange(environment.getDescriptorApiPath(), HttpMethod.PUT,
                    body, String.class, task.getServiceId());
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            log.error("Error send service json to descriptor storage", ex);
            return false;
        }
    }

    private boolean sendTemplates(@NotNull Task task) {
        if (!task.getSendTemplates()) return true;
        if (Objects.isNull(task.getTemplates())) return false;
        return putTemplatesZipFile(task);
    }

    /**
     * Отправка конфигурационного json-файла в каталог услуг
     */
    private boolean sendConfig(@NotNull Task task) {
        if (!task.getSendConfig()) return true;
        if (Objects.isNull(task.getConfig())) return false;
        String configString = new String(task.getConfig());
        JsonNode jsonNode = JsonProcessingUtil.fromJson(configString, JsonNode.class);
        PublishResponse publishResponse = serviceCatalogRestClient.sendServiceConfig(jsonNode);
        return publishResponse.getReturnCode() == 0;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    /**
     * This nested HttpEntity is important to create the correct
     * Content-Disposition entry with metadata "name" and "filename"task
     */
    public boolean putTemplatesZipFile(Task task) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        String fileParam = "template";
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name(fileParam)
                .filename(fileParam)
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(task.getTemplates(), fileMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(fileParam, fileEntity);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        JobProperties.Environment environment = properties.getEnvironments().get(task.getTargetEnvironment());

        if (Objects.isNull(environment) || StringUtils.isEmpty(environment.getTemplatesUrl()))
            return false;

        try {
            restTemplate.exchange(
                    environment.getTemplatesApiPath(),
                    HttpMethod.PUT,
                    requestEntity,
                    String.class,
                    task.getServiceId()
            );
            return true;
        } catch (ResourceAccessException | HttpClientErrorException ex) {
            log.error("Error send templates data to descriptor storage", ex);
            return false;
        }
    }
}
