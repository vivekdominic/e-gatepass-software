package com.vjcet.gatepass_system.controller;

import com.vjcet.gatepass_system.model.LoginRequest;
import com.vjcet.gatepass_system.service.UserService;
import com.vjcet.gatepass_system.model.User; // Import the User model

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Handles user authentication (login).
 */
@RestController
@RequestMapping("/api")
// ABSOLUTE CORS FIX: Allows requests from the Python server (8000) and direct file access.
@CrossOrigin(origins = {"http://localhost:8000", "http://127.0.0.1:8000", "file:///"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private UserService userService; // Note: Use the Service you named 'UserService' if different from 'AuthService'

    /**
     * Endpoint for user login with role-based authentication.
     * @param loginRequest Contains username, password, and role.
     * @return Success response with user role and temporary token/ID, or 401 Unauthorized.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Optional<User> userOpt = userService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                loginRequest.getRole()
        );

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Simple success response (in a real app, this would return a JWT token)
            Map<String, Object> response = Map.of(
                    "message", "Login successful",
                    "token", "dummy_token_" + user.getUsername(),
                    "user", Map.of(
                            "role", user.getRole(),
                            "studentId", user.getStudentId() != null ? user.getStudentId() : user.getUsername()
                    )
            );

            return ResponseEntity.ok(response);
        } else {
            // Failure
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid Username or Password!"));
        }
    }
}