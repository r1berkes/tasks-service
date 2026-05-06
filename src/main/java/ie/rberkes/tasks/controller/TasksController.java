package ie.rberkes.tasks.controller;

import ie.rberkes.tasks.dto.TaskAverageDTO;
import ie.rberkes.tasks.dto.TaskDTO;
import ie.rberkes.tasks.service.TasksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TasksController {

    private final TasksService service;

    @Operation(summary = "Add task")
    @Parameters({
            @Parameter(ref = "#/components/parameters/IdempotencyKey"),
            @Parameter(ref = "#/components/parameters/TraceId")
    })
    @PostMapping
    public ResponseEntity<Void> addTask(
            @RequestBody @Valid TaskDTO req,
            @RequestHeader(name = "Idempotency-Key", required = true) String idemKey
    ) {
        service.addTask(req, idemKey);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{taskName}/stats")
    public ResponseEntity<TaskAverageDTO> stats(@PathVariable String taskName) {
        return ResponseEntity.ok(service.getTaskAverage(taskName));
    }
}