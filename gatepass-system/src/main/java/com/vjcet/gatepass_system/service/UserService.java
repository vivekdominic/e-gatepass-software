package com.vjcet.gatepass_system.service;

import com.vjcet.gatepass_system.model.Student;
import com.vjcet.gatepass_system.model.User;
import com.vjcet.gatepass_system.repository.StudentRepository;
import com.vjcet.gatepass_system.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service layer responsible for user authentication and administrative user-related tasks.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Authenticates a user by checking credentials and validating the requested role.
     * @param username The username (ID).
     * @param password The password (plain text).
     * @param requestedRole The role selected on the login page ('student', 'hod', etc.).
     * @return An Optional containing the User object if authentication is successful, otherwise empty.
     */
    public Optional<User> authenticate(String username, String password, String requestedRole) {

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 1. Check Password (Placeholder check for now)
            if (!user.getPasswordHash().equals(password)) {
                return Optional.empty();
            }

            // 2. Check Role Match
            if (!user.getRole().equalsIgnoreCase(requestedRole)) {
                return Optional.empty();
            }

            // Authentication successful
            return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Fetches the full student profile details.
     */
    public Optional<Student> getStudentProfile(String studentId) {
        return studentRepository.findById(studentId);
    }

    /**
     * Super Admin action: Adds a new Student record and creates a corresponding User account for login.
     * @param student The new Student entity data.
     * @param password The initial password for the student's login account.
     */
    @Transactional
    public void addNewStudent(Student student, String password) {
        // 1. Save the new Student record
        studentRepository.save(student);

        // 2. Create the corresponding User entry for login
        User newUser = new User();
        newUser.setUsername(student.getStudentId());
        newUser.setPasswordHash(password);
        newUser.setRole("student");
        newUser.setStudentId(student.getStudentId());

        userRepository.save(newUser);
    }
}