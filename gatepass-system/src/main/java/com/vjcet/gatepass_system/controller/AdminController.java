package com.vjcet.gatepass_system.controller;

import com.vjcet.gatepass_system.model.Student;
import com.vjcet.gatepass_system.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Handles endpoints for Super Administrator tasks (e.g., adding new users).
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:8000", "http://127.0.0.1:8000", "file:///"}, allowCredentials = "true")
public class AdminController {

    @Autowired
    private UserService userService;

    // DTO for receiving the student details + initial password
    private static class NewStudentRequest {
        private Student student;
        private String initialPassword;

        // Getters and Setters
        public Student getStudent() { return student; }
        public void setStudent(Student student) { this.student = student; }
        public String getInitialPassword() { return initialPassword; }
        public void setInitialPassword(String initialPassword) { this.initialPassword = initialPassword; }
    }

    /**
     * Endpoint for Super Admin to add a new student and create their login account.
     */
    @PostMapping("/add-student")
    public ResponseEntity<Map<String, String>> addStudent(@RequestBody NewStudentRequest request) {

        // Simple check to ensure required data is present
        if (request.getStudent() == null || request.getInitialPassword() == null || request.getStudent().getStudentId() == null) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Missing student data or initial password."));
        }

        try {
            // Call service method to save the student and create the user account
            userService.addNewStudent(request.getStudent(), request.getInitialPassword());

            return ResponseEntity.ok(Collections.singletonMap("message", "Student and User account created successfully."));
        } catch (Exception e) {
            System.err.println("Error adding student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Error saving data. Student ID might already exist."));
        }
    }
}