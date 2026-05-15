package com.collabhub.controller;

import com.collabhub.model.User;
import com.collabhub.security.JwtUtils;
import com.collabhub.service.AuthService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginRequest loginRequest){
        try{
            User verifiedUser = authService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
            String token = jwtUtils.generateToken(verifiedUser.getUsername());
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?>register(@Valid @RequestBody User user){
        try{
           String resultMessage = authService.registerUser(user);
           return ResponseEntity.ok(resultMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

@Data
class LoginRequest{
    private String username;
    private String password;
}
