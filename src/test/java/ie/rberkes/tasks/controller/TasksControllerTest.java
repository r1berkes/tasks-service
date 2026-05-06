package ie.rberkes.tasks.controller;

import ie.rberkes.tasks.dto.TaskAverageDTO;
import ie.rberkes.tasks.exception.DuplicateRequestException;
import ie.rberkes.tasks.exception.GlobalExceptionHandler;
import ie.rberkes.tasks.service.TasksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TasksControllerTest {

    MockMvc mvc;
    TasksService service;

    @BeforeEach
    void setup() {
        service = mock(TasksService.class);
        TasksController controller = new TasksController(service);

        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldFailWithInvalidBody() throws Exception {
        mvc.perform(post("/v1/tasks")
                        .header("Idempotency-Key", "k1")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        mvc.perform(post("/v1/tasks")
                        .header("Idempotency-Key", "k1")
                        .content("""
                                {
                                  "taskName": "task1",
                                  "duration": 100
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).addTask(any(), eq("k1"));
    }

    @Test
    void shouldReturnConflictOnDuplicate() throws Exception {
        doThrow(new DuplicateRequestException("duplicate"))
                .when(service).addTask(any(), eq("k1"));

        mvc.perform(post("/v1/tasks")
                        .header("Idempotency-Key", "k1")
                        .content("""
                                {
                                  "taskName": "task1",
                                  "duration": 100
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnStats() throws Exception {
        when(service.getTaskAverage("task1"))
                .thenReturn(new TaskAverageDTO("task1", 2L, 50.0));

        mvc.perform(get("/v1/tasks/task1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("task1"))
                .andExpect(jsonPath("$.counter").value(2))
                .andExpect(jsonPath("$.avgDuration").value(50.0));
    }

    @Test
    void shouldReturn500OnUnexpectedError() throws Exception {
        doThrow(new RuntimeException("boom"))
                .when(service).addTask(any(), any());

        mvc.perform(post("/v1/tasks")
                        .header("Idempotency-Key", "k1")
                        .content("""
                                {
                                  "taskName": "task1",
                                  "duration": 100
                                }
                                """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}