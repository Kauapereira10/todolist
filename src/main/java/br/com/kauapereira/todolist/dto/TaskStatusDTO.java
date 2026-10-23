package br.com.kauapereira.todolist.dto;

import br.com.kauapereira.todolist.model.enums.Status;
import jakarta.validation.constraints.NotNull;

public record TaskStatusDTO(

        @NotNull(message = "Status is required (TO_DO, IN_PROGRESS, COMPLETED, CANCELLED)")
        Status status
) {
}
