package br.com.kauapereira.todolist.service;

import br.com.kauapereira.todolist.dto.*;
import br.com.kauapereira.todolist.exception.ResourceNotFoundException;
import br.com.kauapereira.todolist.mappers.TaskMapper;
import br.com.kauapereira.todolist.model.Task;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import br.com.kauapereira.todolist.repository.TaskRepository;
import br.com.kauapereira.todolist.validator.TaskValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TaskService {

    private final TaskRepository repository;
    private final TaskValidator validator;
    private final TaskMapper mapper;

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO) {
        validator.createTask(requestDTO);
        Task task = mapper.toEntity(requestDTO);
        Task saved = repository.save(task);
        return mapper.toDto(saved);
    }

    public List<TaskResponseDTO> listTasks() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Map<Priority, List<TaskResponseDTO>> listTasksByPriority(Priority priority) {
        return repository.findAll().stream()
                .filter(task -> task.getPriority().equals(priority))
                .map(mapper::toDto)
                .collect(Collectors.groupingBy(TaskResponseDTO::priority));
    }

    public TaskResponseDTO findByTaskId(Long id) {
        return mapper.toDto(findTaskById(id));
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO) {
        validator.updateTask(requestDTO.title());
        Task task = findTaskById(id);
        mapper.updateTaskFromDto(requestDTO, task);
        return mapper.toDto(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = findTaskById(id);
        repository.delete(task);
    }

    @Transactional
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatusDTO statusDTO) {
        Task task = findTaskById(id);
        mapper.updateStatusFromDto(statusDTO, task);
        Task updated = repository.save(task);
        return mapper.toDto(updated);
    }

    @Transactional
    public TaskResponseDTO updateTaskDueDate(Long id, TaskDueDateDTO dueDateDTO) {
        Task task = findTaskById(id);
        mapper.updateDueDateFromDto(dueDateDTO, task);
        Task updated = repository.save(task);
        return mapper.toDto(updated);
    }

    @Transactional
    public TaskResponseDTO updateTaskPriority(Long id, TaskPriorityDTO priorityDtop) {
        Task task = findTaskById(id);
        mapper.updatePriorityFromDto(priorityDtop, task);
        Task updated = repository.save(task);
        return mapper.toDto(updated);
    }

    private Task findTaskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }
}