package br.com.kauapereira.todolist.mappers;

import br.com.kauapereira.todolist.dto.TaskRequestDTO;
import br.com.kauapereira.todolist.dto.TaskResponseDTO;
import br.com.kauapereira.todolist.model.Task;
import org.mapstruct.Mapper;

@Mapper
public interface TaskMapper {

    TaskResponseDTO toDto(Task task);

    Task toEntity(Task taskResponseDTO);

    Task updateTaskFromDto(TaskRequestDTO requestDTO, Task task);

}
