package ru.gosuslugi.pgu.service.publisher.core.api.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.gosuslugi.pgu.service.publisher.job.JobProperties;
import ru.gosuslugi.pgu.service.publisher.job.JobService;
import ru.gosuslugi.pgu.service.publisher.job.dto.TaskCreateDto;
import ru.gosuslugi.pgu.service.publisher.job.dto.TaskInfoDto;
import ru.gosuslugi.pgu.service.publisher.vcs.VcsService;
import ru.gosuslugi.pgu.service.publisher.vcs.dto.GitTagDto;
import ru.gosuslugi.pgu.service.publisher.vcs.dto.ServiceListRequestDto;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "v1/publisher", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublisherController {

    private final VcsService vcsService;
    private final JobService jobService;

    @GetMapping(value = "/services")
    public Map<String, String> services() {
        return vcsService.getServicesListFromConfig();
    }

    @PostMapping(value = "/services")
    public Map<String, String> services(@RequestBody ServiceListRequestDto requestDto) {
        return vcsService.getServicesList(requestDto.getVersion());
    }

    @GetMapping(value = "/versions")
    public List<GitTagDto> versions() {
        return vcsService.getVersions();
    }

    @GetMapping(value = "/environments")
    public Map<String, JobProperties.Environment> environments() {
        return jobService.getEnvironments();
    }

    @GetMapping(value = "/tasks")
    public List<TaskInfoDto> tasks() {
        return jobService.getTasks();
    }

    @PostMapping(value = "/tasks")
    public TaskInfoDto createPublishTask(@RequestBody TaskCreateDto taskCreateDto) {
        return jobService.createTask(taskCreateDto);
    }

}
