package br.com.kauapereira.todolist.dto;

import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequestDTO(

    @NotEmpty(message = "Title cannot be empty")
    @Size(max = 100, min = 3, message = "Title must be between 3 and 100 characters")
    String title,

    @Size(max = 200, message = "Description must be at most 200 characters")
    String description,

    @NotEmpty(message = "Status cannot be empty (TO_DO, IN_PROGRESS, COMPLETED, CANCELLED)")
    Status status,

    @NotNull(message = "Priority cannot be null (NONE, LOW, MEDIUM, HIGH)")
    Priority priority,

    @NotEmpty(message = "Due date cannot be empty")
    LocalDate dueDate
) {
}
