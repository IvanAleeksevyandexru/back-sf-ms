package ru.gosuslugi.pgu.service.publisher.job;

import ru.gosuslugi.pgu.service.publisher.job.dto.TaskCreateDto;
import ru.gosuslugi.pgu.service.publisher.job.dto.TaskInfoDto;

import java.util.List;
import java.util.Map;

public interface JobService {

    TaskInfoDto createTask(TaskCreateDto taskCreateDto);

    void processTask();

    List<TaskInfoDto> getTasks();

    Map<String, JobProperties.Environment> getEnvironments();

}
