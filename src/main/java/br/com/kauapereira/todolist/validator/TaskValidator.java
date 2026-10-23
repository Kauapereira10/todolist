package br.com.kauapereira.todolist.validator;

import br.com.kauapereira.todolist.dto.TaskRequestDTO;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskValidator {

    public void createTask(TaskRequestDTO dto) {
        if (dto.title() == null || dto.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (dto.priority() == null) {
            throw new IllegalArgumentException("Priority is required");
        }
        if (dto.dueDate() == null) {
            throw new IllegalArgumentException("Due date is required");
        }
    }

    public void updateTask(String title) {
        if (title != null && title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
    }

    public void updateTaskStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }
    }

    public void updateTaskPriority(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority is required");
        }
    }

    public void updateTaskDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date is required");
        }
    }
}