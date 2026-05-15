package com.collabhub.service;

import com.collabhub.controller.ResourceNotFoundException;
import com.collabhub.model.ProfileUpdateRequest;
import com.collabhub.model.Project;
import com.collabhub.model.User;
import com.collabhub.model.UserProfileResponse;
import com.collabhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getMyProfile(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    public UserProfileResponse updateMyProfile(String username, ProfileUpdateRequest request){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getEmail() != null && !request.getEmail().isBlank()){
            user.setEmail(request.getEmail());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("User {} updated their password", username);
        }
        userRepository.save(user);
        return getMyProfile(username);
    }

    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
        log.info("Admin updated role for user {} to {}", user.getUsername(), newRole);
    }

    public void toggleUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(isActive);
        userRepository.save(user);
        log.info("Admin changed status for user {} to active={}", user.getUsername(), isActive);
    }

    private UserProfileResponse mapToResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        
        if (user.getProjects() != null) {
            response.setProjectNames(user.getProjects().stream()
                    .map(Project::getName)
                    .collect(Collectors.toList()));
        }

        return response;
    }
}
