package com.collabhub.service;

import com.collabhub.model.Task;
import com.collabhub.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCleanupService {

    private final TaskRepository taskRepository;

    @Scheduled(fixedRate = 10000)
    public void markOverdueTask(){
        log.info("Starting background job: Checking for overdue tasks");

        List<Task> allTasks = taskRepository.findAll();
        int count = 0;
        for(Task task : allTasks){
            if (task.getDeadline() != null && task.getDeadline().isBefore(LocalDateTime.now()) && !task.getStatus().equals("OVERDUE") && !task.getStatus().equals("COMPLETED")){
                task.setStatus("OVERDUE");
                taskRepository.save(task);
                count++;
            }
        }
        log.info("Background job finished. Marked {} tasks as overdue", count);
    }
}
