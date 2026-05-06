package ie.rberkes.tasks.service;

import ie.rberkes.tasks.dto.TaskAverageDTO;
import ie.rberkes.tasks.dto.TaskDTO;
import ie.rberkes.tasks.domain.TasksAvg;
import ie.rberkes.tasks.exception.DuplicateRequestException;
import ie.rberkes.tasks.repo.IdempotencyRepository;
import ie.rberkes.tasks.repo.TasksAvgRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksServiceTest {

    @Mock
    private TasksAvgRepository tasksRepo;

    @Mock
    private IdempotencyRepository idemRepo;

    @InjectMocks
    private TasksService service;

    @Test
    void shouldInsertWhenIdempotencyKeyIsNew() {
        when(idemRepo.insertIfNotExists("k"))
                .thenReturn(1);

        service.addTask(new TaskDTO("task1", 100L), "k");

        verify(tasksRepo)
                .upsertTask("task1", 100L);
    }

    @Test
    void shouldThrowOnDuplicate() {
        when(idemRepo.insertIfNotExists("k"))
                .thenReturn(0);

        assertThrows(
                DuplicateRequestException.class,
                () -> service.addTask(new TaskDTO("task1", 100L), "k")
        );

        verify(tasksRepo, never())
                .upsertTask(anyString(), anyLong());
    }

    @Test
    void shouldPropagateRepositoryError() {
        when(idemRepo.insertIfNotExists("k"))
                .thenReturn(1);

        doThrow(new RuntimeException("db fail"))
                .when(tasksRepo)
                .upsertTask(anyString(), anyLong());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.addTask(new TaskDTO("task1", 100L), "k")
        );

        assertEquals("db fail", ex.getMessage());
    }

    @Test
    void shouldReturnStats() {
        TasksAvg entity = new TasksAvg();
        entity.setTaskName("task1");
        entity.setCounter(2L);
        entity.setAvgDuration(50.0);

        when(tasksRepo.findById("task1"))
                .thenReturn(Optional.of(entity));

        TaskAverageDTO result = service.getTaskAverage("task1");

        assertEquals("task1", result.taskName());
        assertEquals(2L, result.counter());
        assertEquals(50.0, result.avgDuration());
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        when(tasksRepo.findById("task1"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> service.getTaskAverage("task1")
        );
    }
}