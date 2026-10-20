package br.com.kauapereira.todolist.service;

import br.com.kauapereira.todolist.dto.TaskResponseDTO;
import br.com.kauapereira.todolist.model.Task;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import br.com.kauapereira.todolist.repository.TaskRepository;
import br.com.kauapereira.todolist.validator.TaskValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@AllArgsConstructor
@Service
public class TaskService {

    private final TaskRepository repository;
    private final TaskValidator validator;

    public Task createTask(String name, String description, Status status, Priority priority, LocalDate dueDate) throws IllegalAccessException {
        validator.createTask(name, description, status, priority, dueDate);
        Task task = new Task(name, description, status, priority, dueDate);
        repository.save(task);
        return task;
    }

}
