package com.vjcet.gatepass_system.controller;

import com.vjcet.gatepass_system.model.Student;
import com.vjcet.gatepass_system.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Handles API endpoints related to student profile data retrieval.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:8000", "http://127.0.0.1:8000", "file:///"}, allowCredentials = "true")
public class StudentController {

    @Autowired
    private UserService userService;

    /**
     * Fetches the profile data for a specific student ID.
     * Used by the student-profile.html dashboard on load.
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Student> getStudentProfile(@PathVariable String studentId) {

        // Note: The UserService handles fetching the Student entity from the database
        Optional<Student> studentOpt = userService.getStudentProfile(studentId);

        return studentOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}