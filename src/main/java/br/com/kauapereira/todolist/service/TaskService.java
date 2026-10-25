package br.com.kauapereira.todolist.service;

import br.com.kauapereira.todolist.dto.*;
import br.com.kauapereira.todolist.exception.InvalidStatusTransitionException;
import br.com.kauapereira.todolist.exception.ResourceNotFoundException;
import br.com.kauapereira.todolist.mappers.TaskMapper;
import br.com.kauapereira.todolist.model.Task;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import br.com.kauapereira.todolist.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        Task task = mapper.toEntity(requestDTO);
        return mapper.toDto(repository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> listTasks(Status status, Priority priority) {
        return repository.findByFilters(status, priority).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO findByTaskId(Long id) {
        return mapper.toDto(findTaskById(id));
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO) {
        Task task = findTaskById(id);
        mapper.updateTaskFromDto(requestDTO, task);
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatusDTO statusDTO) {
        Task task = findTaskById(id);
        Status current = task.getStatus();
        Status target = statusDTO.status();

        if (current == target) {
            return mapper.toDto(task);
        }
        if (!current.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(
                    "Cannot change status from " + current + " to " + target);
        }

        mapper.updateStatusFromDto(statusDTO, task);
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponseDTO updateTaskPriority(Long id, TaskPriorityDTO priorityDTO) {
        Task task = findTaskById(id);
        mapper.updatePriorityFromDto(priorityDTO, task);
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponseDTO updateTaskDueDate(Long id, TaskDueDateDTO dueDateDTO) {
        Task task = findTaskById(id);
        mapper.updateDueDateFromDto(dueDateDTO, task);
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        repository.delete(findTaskById(id));
    }

    private Task findTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }
}