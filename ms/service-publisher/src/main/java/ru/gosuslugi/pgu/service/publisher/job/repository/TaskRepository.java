package ru.gosuslugi.pgu.service.publisher.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;
import ru.gosuslugi.pgu.service.publisher.job.model.Task;
import ru.gosuslugi.pgu.service.publisher.job.model.TaskStatus;

import javax.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Transactional
    Optional<Task> findFirstByCreatedGreaterThanAndStatus(LocalDateTime created, TaskStatus status);

}
