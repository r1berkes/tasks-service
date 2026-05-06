package ie.rberkes.tasks;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Disabled("Requires Docker/Testcontainers")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class TasksApplicationTests {

	@Test
	void contextLoads() {
	}
}
