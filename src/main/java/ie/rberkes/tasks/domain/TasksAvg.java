package ie.rberkes.tasks.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks_avg")
@Getter
@Setter
@NoArgsConstructor
public class TasksAvg extends BaseEntity {

    @Id
    private String taskName;
    private long counter;
    private double avgDuration;
}