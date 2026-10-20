package br.com.kauapereira.todolist.validator;

import br.com.kauapereira.todolist.exception.ResourceNotFoundException;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;

import java.time.LocalDate;

public class TaskValidator {

    public void createTask(String name, String description, Status status, Priority priority, LocalDate dueDate) throws IllegalAccessException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalAccessException("Task name is required");
        }
        if (!status.equals(Status.TO_DO) && !status.equals(Status.IN_PROGRESS) && !status.equals(Status.COMPLETED) && !status.equals(Status.CANCELLED)) {
            throw new IllegalAccessException("Invalid status value");
        }
        if (!priority.equals(Priority.NONE) && !priority.equals(Priority.LOW) && !priority.equals(Priority.MEDIUM) && !priority.equals(Priority.HIGH)) {
            throw new IllegalAccessException("Invalid priority value");
        }
        if (dueDate == null) {
            throw new IllegalAccessException("Due date is required");
        }
    }

    public void findByTaskId(Long id) throws ResourceNotFoundException {
        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Invalid task ID");
        }
    }

}
