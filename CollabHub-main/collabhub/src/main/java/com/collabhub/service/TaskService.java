package com.collabhub.service;

import com.collabhub.controller.ResourceNotFoundException;
import com.collabhub.model.Project;
import com.collabhub.model.Task;
import com.collabhub.model.User;
import com.collabhub.repository.ProjectRepository;
import com.collabhub.repository.TaskRepository;
import com.collabhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public Task createTaskForProject(Long projectId, Task task, String creatorUsername) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + creatorUsername));

        task.setProject(project);
        task.setCreator(creator);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        log.info("Task '{}' created in project {} by user {}", task.getTitle(), projectId, creatorUsername);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (updatedTask.getTitle() != null) {
            existingTask.setTitle(updatedTask.getTitle());
        }
        if (updatedTask.getDescription() != null) {
            existingTask.setDescription(updatedTask.getDescription());
        }
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getStatus() != null) {
            existingTask.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getDeadline() != null) {
            existingTask.setDeadline(updatedTask.getDeadline());
        }

        existingTask.setUpdatedAt(LocalDateTime.now());
        log.info("Task {} updated", taskId);
        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        taskRepository.delete(task);
        log.info("Task {} '{}' deleted", taskId, task.getTitle());
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return taskRepository.findByProject_Id(projectId);
    }

    public List<Task> getTasksFiltered(Long projectId, String status, String priority) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        if (status != null && priority != null) {
            return taskRepository.findByProject_IdAndStatusAndPriority(projectId, status, priority);
        } else if (status != null) {
            return taskRepository.findByProject_IdAndStatus(projectId, status);
        } else if (priority != null) {
            return taskRepository.findByProject_IdAndPriority(projectId, priority);
        }
        return taskRepository.findByProject_Id(projectId);
    }

    public Task assignUserToTask(Long taskId, String usernameToAssign) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        User user = userRepository.findByUsername(usernameToAssign)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + usernameToAssign));

        task.setAssignee(user);
        task.setUpdatedAt(LocalDateTime.now());
        log.info("Task {} assigned to user {}", taskId, usernameToAssign);
        return taskRepository.save(task);
    }
}
