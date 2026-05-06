package ie.rberkes.tasks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@Disabled("Requires Docker/Testcontainers")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TasksIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("tasks")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    void shouldCreateTaskAndCalculateAverage() throws Exception {

        String body1 = """
            {
              "taskName": "import-job",
              "duration": 100
            }
            """;

        String body2 = """
            {
              "taskName": "import-job",
              "duration": 300
            }
            """;

        mvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "req-1")
                        .content(body1))
                .andExpect(status().isCreated());

        mvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "req-2")
                        .content(body2))
                .andExpect(status().isCreated());

        mvc.perform(get("/v1/tasks/import-job/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskName").value("import-job"))
                .andExpect(jsonPath("$.averageDuration").value(200.0));
    }

    @Test
    void shouldIgnoreDuplicateIdempotencyKey() throws Exception {

        String body = """
            {
              "taskName": "duplicate-job",
              "duration": 100
            }
            """;

        mvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "same-key")
                        .content(body))
                .andExpect(status().isCreated());

        mvc.perform(post("/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Request-Id", "same-key")
                        .content(body))
                .andExpect(status().isCreated());

        mvc.perform(get("/v1/tasks/duplicate-job/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageDuration").value(100.0));
    }
}