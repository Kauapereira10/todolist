package br.com.kauapereira.todolist.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(

        @NotBlank(message = "Name cannot be blank")
        @Size(max = 50, message = "Name must be at most 50 characters")
        String name
) {
}
