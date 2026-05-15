package com.collabhub.controller;

import com.collabhub.model.Project;
import com.collabhub.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getProjects(){
        return ResponseEntity.ok(projectService.getAllActiveProjects());
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project, Principal principal){
        return ResponseEntity.ok(projectService.createProject(project, principal.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id){
        projectService.softDelete(id);
        return ResponseEntity.ok("Project deleted successfully!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project project){
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Project> addMember(@PathVariable Long id, @RequestBody Map<String, String> requestBody){
        String username = requestBody.get("username");
        return ResponseEntity.ok(projectService.addMemberToProject(id, username));
    }
}
