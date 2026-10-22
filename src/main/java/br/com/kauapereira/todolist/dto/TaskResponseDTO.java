package br.com.kauapereira.todolist.dto;

import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;

import java.time.LocalDate;

public record TaskResponseDTO(
    Long id,
    String title,
    String description,
    Status status,
    Priority priority,
    LocalDate dueDate,
    LocalDate createdAt,
    LocalDate updatedAt
) {
}
