package com.collabhub.controller;

import com.collabhub.model.ProfileUpdateRequest;
import com.collabhub.model.UserProfileResponse;
import com.collabhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Principal principal){
        return ResponseEntity.ok(userService.getMyProfile(principal.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUser(@Valid @RequestBody ProfileUpdateRequest request, Principal principal){
        return ResponseEntity.ok(userService.updateMyProfile(principal.getName(), request));
    }

}
