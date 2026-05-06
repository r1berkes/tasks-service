package ie.rberkes.tasks.repo;

import ie.rberkes.tasks.domain.TasksAvg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TasksAvgRepository extends JpaRepository<TasksAvg, String> {

    @Modifying
    @Query(value = """
            INSERT INTO tasks_avg (task_name, counter, avg_duration)
                   VALUES (:taskName, 1, :duration)
                   ON CONFLICT (task_name)
                   DO UPDATE SET
                       counter = tasks_avg.counter + 1,
                       avg_duration = ROUND(
                              (
                                  (
                                      (
                                          tasks_avg.avg_duration * tasks_avg.counter
                                      ) + EXCLUDED.avg_duration
                                  ) / (tasks_avg.counter +1)
                              )::numeric,
                              3
                          ),
                       updated_at = now();
            """, nativeQuery = true)
    void upsertTask(String taskName, long duration);
}