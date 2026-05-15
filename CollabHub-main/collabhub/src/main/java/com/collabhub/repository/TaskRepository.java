package com.collabhub.repository;

import com.collabhub.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProject_Id(Long projectId);
    List<Task> findByStatusAndPriority(String status, String priority);
    List<Task> findByProject_IdAndStatus(Long projectId, String status);
    List<Task> findByProject_IdAndPriority(Long projectId, String priority);
    List<Task> findByProject_IdAndStatusAndPriority(Long projectId, String status, String priority);
}
