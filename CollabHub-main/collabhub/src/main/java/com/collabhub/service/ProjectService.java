package com.collabhub.service;

import com.collabhub.controller.ResourceNotFoundException;
import com.collabhub.model.Project;
import com.collabhub.model.User;
import com.collabhub.repository.ProjectRepository;
import com.collabhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public List<Project> getAllActiveProjects(){
        return projectRepository.findByIsDeletedFalse();
    }

    public Project createProject(Project project, String username){
        User owner = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        project.setOwner(owner);
        if (project.getIsDeleted() == null) {
            project.setIsDeleted(false);
        }
        log.info("Project '{}' created by user {}", project.getName(), username);
        return projectRepository.save(project);
    }

    public void softDelete(Long id){
        Project project = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setIsDeleted(true);
        projectRepository.save(project);
        log.info("Project {} '{}' soft-deleted", id, project.getName());
    }

    public Project updateProject(Long id, Project updatedProject){
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        if (updatedProject.getStatus() != null) {
            existingProject.setStatus(updatedProject.getStatus());
        }
        log.info("Project {} updated", id);
        return projectRepository.save(existingProject);
    }

    public Project addMemberToProject(Long projectId, String usernameToAdd){
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User newMember = userRepository.findByUsername(usernameToAdd).orElseThrow(() -> new ResourceNotFoundException("User not found: " + usernameToAdd));

        if (!project.getMembers().contains(newMember)){
            project.getMembers().add(newMember);
            log.info("User {} was added as a member to project {}", usernameToAdd, projectId);
        }

        return projectRepository.save(project);
    }

}
