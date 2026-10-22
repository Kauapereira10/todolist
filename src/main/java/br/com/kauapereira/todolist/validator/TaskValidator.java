package br.com.kauapereira.todolist.validator;

import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskValidator {

    public void createTask(String title, String description, Status status, Priority priority, LocalDate dueDate) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority is required");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date is required");
        }
    }

    public void updateTask(String title) {
        if (title != null && title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
    }
}