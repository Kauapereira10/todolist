package br.com.kauapereira.todolist.dto;

import br.com.kauapereira.todolist.model.enums.Priority;
import jakarta.validation.constraints.NotNull;

public record TaskPriorityDTO(

        @NotNull(message = "Priority cannot be null (NONE, LOW, MEDIUM, HIGH)")
        Priority priority
) {
}
