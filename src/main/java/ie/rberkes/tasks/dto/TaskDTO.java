package ie.rberkes.tasks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TaskDTO(
        @NotBlank
        String taskName,

        @NotNull
        @Positive
        Long duration
) {}