package com.vjcet.gatepass_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Lombok annotations for getters, setters, and constructors
@Data
@NoArgsConstructor
@AllArgsConstructor
// JPA annotation mapping this class to the 'users' table
@Entity
@Table(name = "users")
public class User {

    // Primary Key mapping to the username column
    @Id
    private String username;

    // Stores the password hash (or placeholder in our case)
    private String passwordHash;

    // Stores the user's role: 'student', 'hod', 'security', 'warden'
    private String role;

    // Foreign key linking back to the Student table for student roles
    private String studentId;
}