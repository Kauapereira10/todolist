package br.com.kauapereira.todolist.dto;

import br.com.kauapereira.todolist.model.enums.Priority;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record TaskRequestDTO(

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, min = 3, message = "Title must be between 3 and 100 characters")
    String title,

    @Size(max = 200, message = "Description must be at most 200 characters")
    String description,

    @NotNull(message = "Priority cannot be null (NONE, LOW, MEDIUM, HIGH)")
    Priority priority,

    @NotNull(message = "Due date cannot be empty")
    LocalDate dueDate
) {
}
