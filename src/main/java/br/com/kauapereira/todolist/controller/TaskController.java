package br.com.kauapereira.todolist.controller;

import br.com.kauapereira.todolist.dto.*;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import br.com.kauapereira.todolist.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        TaskResponseDTO responseDTO = service.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> listTasks(
            @RequestParam(name = "status", required = false) Status status,
            @RequestParam(name = "priority", required = false) Priority priority
    ) {
        return ResponseEntity.ok(service.listTasks(status, priority));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findByTaskId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable("id") Long id,
            @Valid @RequestBody TaskRequestDTO dto
    ) {
        return ResponseEntity.ok(service.updateTask(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable("id") Long id,
            @Valid @RequestBody TaskStatusDTO dto
    ) {
        return ResponseEntity.ok(service.updateTaskStatus(id, dto));
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskResponseDTO> updateTaskPriority(
            @PathVariable("id") Long id,
            @Valid @RequestBody TaskPriorityDTO dto
    ) {
        return ResponseEntity.ok(service.updateTaskPriority(id, dto));
    }

    @PatchMapping("/{id}/dueDate")
    public ResponseEntity<TaskResponseDTO> updateDueDate(
            @PathVariable("id") Long id,
            @Valid @RequestBody TaskDueDateDTO dto
    ) {
       return ResponseEntity.ok(service.updateTaskDueDate(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        service.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}