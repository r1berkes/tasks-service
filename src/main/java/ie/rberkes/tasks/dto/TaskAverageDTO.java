package ie.rberkes.tasks.dto;

public record TaskAverageDTO(
        String taskName,
        long counter,
        double avgDuration
) {}