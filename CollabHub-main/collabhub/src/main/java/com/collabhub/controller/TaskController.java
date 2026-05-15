package com.collabhub.controller;

import com.collabhub.model.Task;
import com.collabhub.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@PathVariable Long projectId, @Valid @RequestBody Task task, Principal principal) {
        return ResponseEntity.ok(taskService.createTaskForProject(projectId, task, principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        if (status != null || priority != null) {
            return ResponseEntity.ok(taskService.getTasksFiltered(projectId, status, priority));
        }
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(taskId, task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<Task> assignTaskToUser(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Map<String, String> requestBody) {
        String username = requestBody.get("username");
        return ResponseEntity.ok(taskService.assignUserToTask(taskId, username));
    }
}
