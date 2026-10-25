package br.com.kauapereira.todolist.exception;

import java.util.List;

public record FieldsErrorsDTO(
        String field,
        String mensage
) {

    public static List<FieldsErrorsDTO> errorsDTO(List<FieldsErrorsDTO> errorsDTOS) {
        errorsDTOS.stream()
                .filter(fieldsErrorsDTO ->  fieldsErrorsDTO.field().equals(fieldsErrorsDTO.mensage))
                .toList();
        return errorsDTOS;
    }
}
