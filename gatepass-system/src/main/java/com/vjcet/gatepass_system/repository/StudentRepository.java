package com.vjcet.gatepass_system.repository;

import com.vjcet.gatepass_system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Student entities.
 * Extends JpaRepository to inherit basic CRUD (Create, Read, Update, Delete)
 * operations for the Student table using the primary key (String studentId).
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    // No custom methods are needed here, as the default methods handle fetching,
    // saving, and updating Student entities based on their studentId.
}