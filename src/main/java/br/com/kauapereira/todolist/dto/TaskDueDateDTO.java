package br.com.kauapereira.todolist.dto;

import br.com.kauapereira.todolist.model.enums.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskDueDateDTO(

        @NotNull(message = "Due date cannot be empty")
        @FutureOrPresent(message = "Due date must be today or in the future")
        LocalDate dueDate
) {
}
