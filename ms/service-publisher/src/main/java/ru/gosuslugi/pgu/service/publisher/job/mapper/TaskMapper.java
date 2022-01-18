package ru.gosuslugi.pgu.service.publisher.job.mapper;

import org.mapstruct.Mapper;
import ru.gosuslugi.pgu.service.publisher.job.dto.TaskCreateDto;
import ru.gosuslugi.pgu.service.publisher.job.dto.TaskInfoDto;
import ru.gosuslugi.pgu.service.publisher.job.model.Task;

@Mapper
public interface TaskMapper {

    TaskCreateDto toDto(Task task);

    TaskInfoDto toInfoDto(Task task);

    Task toTask(TaskCreateDto dto);

}
