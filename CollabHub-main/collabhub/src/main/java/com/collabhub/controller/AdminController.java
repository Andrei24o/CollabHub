package com.collabhub.controller;


import com.collabhub.model.UserProfileResponse;
import com.collabhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<String> changeUserRole(@PathVariable Long id, @RequestParam String role) {
        userService.updateUserRole(id, role);
        return ResponseEntity.ok("User role updated successfully to " + role);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        userService.toggleUserStatus(id, active);
        return ResponseEntity.ok("User active status changed to " + active);
    }
}
