package br.com.kauapereira.todolist.dto.task;

import jakarta.validation.constraints.NotNull;

public record TaskCategoryDTO(

        @NotNull(message = "Category id is required")
        Long categoryId
) {
}
