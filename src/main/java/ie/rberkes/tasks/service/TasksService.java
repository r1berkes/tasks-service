package ie.rberkes.tasks.service;


import ie.rberkes.tasks.exception.DuplicateRequestException;
import ie.rberkes.tasks.domain.TasksAvg;
import ie.rberkes.tasks.dto.TaskAverageDTO;
import ie.rberkes.tasks.dto.TaskDTO;
import ie.rberkes.tasks.repo.IdempotencyRepository;
import ie.rberkes.tasks.repo.TasksAvgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TasksService {

    private final TasksAvgRepository tasksRepo;
    private final IdempotencyRepository idemRepo;

    @Transactional
    public void addTask(TaskDTO taskDTO, String idemKey) {
        log.info("Processing task {}", taskDTO.taskName());
        if (idemRepo.insertIfNotExists(idemKey) == 0) {
            throw new DuplicateRequestException("Task already inserted");
        }

        try {
            tasksRepo.upsertTask(taskDTO.taskName(), taskDTO.duration());
        } catch (Exception e) {
            log.error("Failed processing task {}", taskDTO.taskName(), e);
            throw e;
        }
    }

    public TaskAverageDTO getTaskAverage(String taskName) {
        TasksAvg entity = tasksRepo.findById(taskName)
                .orElseThrow(() -> new RuntimeException("Not found"));
        return new TaskAverageDTO(
                entity.getTaskName(),
                entity.getCounter(),
                entity.getAvgDuration()
        );
    }
}
