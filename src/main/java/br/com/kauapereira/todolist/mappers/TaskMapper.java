package br.com.kauapereira.todolist.mappers;

import br.com.kauapereira.todolist.dto.task.TaskDueDateDTO;
import br.com.kauapereira.todolist.dto.task.TaskPriorityDTO;
import br.com.kauapereira.todolist.dto.task.TaskRequestDTO;
import br.com.kauapereira.todolist.dto.task.TaskResponseDTO;
import br.com.kauapereira.todolist.dto.task.TaskStatusDTO;
import br.com.kauapereira.todolist.model.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponseDTO toDto(Task task);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "TO_DO")
    @Mapping(target = "category", ignore = true)
    Task toEntity(TaskRequestDTO requestDTO);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "category", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromDto(TaskRequestDTO requestDTO, @MappingTarget Task task);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateStatusFromDto(TaskStatusDTO requestDTO, @MappingTarget Task task);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updatePriorityFromDto(TaskPriorityDTO priorityDTO, @MappingTarget Task task);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateDueDateFromDto(TaskDueDateDTO dueDateDTO, @MappingTarget Task task);

}
