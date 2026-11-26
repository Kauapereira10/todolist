package br.com.kauapereira.todolist.service;

import br.com.kauapereira.todolist.dto.task.TaskCategoryDTO;
import br.com.kauapereira.todolist.dto.task.TaskDueDateDTO;
import br.com.kauapereira.todolist.dto.task.TaskPriorityDTO;
import br.com.kauapereira.todolist.dto.task.TaskRequestDTO;
import br.com.kauapereira.todolist.dto.task.TaskResponseDTO;
import br.com.kauapereira.todolist.dto.task.TaskStatusDTO;
import br.com.kauapereira.todolist.exception.InvalidStatusTransitionException;
import br.com.kauapereira.todolist.exception.ResourceNotFoundException;
import br.com.kauapereira.todolist.mappers.TaskMapper;
import br.com.kauapereira.todolist.model.Category;
import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.model.Task;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import br.com.kauapereira.todolist.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO requestDTO, Owner owner) {
        Task task = mapper.toEntity(requestDTO);
        task.setOwner(owner);
        task.setCategory(findCategoryById(requestDTO.categoryId(), owner));
        return mapper.toDto(repository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> listTasks(Status status, Priority priority, Long categoryId, Owner owner) {
        return repository.findAllByFilters(status, priority, categoryId, owner).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO findByTaskId(Long id, Owner owner) {
        return mapper.toDto(findTaskById(id, owner));
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO requestDTO, Owner owner) {
        Task task = findTaskById(id, owner);
        mapper.updateTaskFromDto(requestDTO, task);
        task.setCategory(findCategoryById(requestDTO.categoryId(), owner));
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponseDTO updateTaskStatus(Long id, TaskStatusDTO statusDTO, Owner owner) {
        Task task = findTaskById(id, owner);
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
    public TaskResponseDTO updateTaskPriority(Long id, TaskPriorityDTO priorityDTO, Owner owner) {
        Task task = findTaskById(id, owner);
        mapper.updatePriorityFromDto(priorityDTO, task);
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponseDTO updateTaskDueDate(Long id, TaskDueDateDTO dueDateDTO, Owner owner) {
        Task task = findTaskById(id, owner);
        mapper.updateDueDateFromDto(dueDateDTO, task);
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public TaskResponseDTO updateTaskCategory(Long id, TaskCategoryDTO taskCategoryDTO, Owner owner) {
        Task task = findTaskById(id, owner);
        Category category = findCategoryById(taskCategoryDTO.categoryId(), owner);

        Long currentCategoryId = task.getCategory() != null ? task.getCategory().getId() : null;
        if (category.getId().equals(currentCategoryId)) {
            return mapper.toDto(task);
        }

        task.setCategory(category);
        return mapper.toDto(repository.saveAndFlush(task));
    }

    @Transactional
    public void deleteTask(Long id, Owner owner) {
        repository.delete(findTaskById(id, owner));
    }

    private Task findTaskById(Long id, Owner owner) {
        return repository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    private Category findCategoryById(Long categoryId, Owner owner) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndOwnerId(categoryId, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }
}