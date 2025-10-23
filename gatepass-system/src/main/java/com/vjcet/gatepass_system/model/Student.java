package com.vjcet.gatepass_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Database Entity representing the 'students' table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
// JPA annotation mapping this class to the 'students' table
@Entity
@Table(name = "students")
public class Student {

    // Primary Key mapping to the student_id column
    @Id
    @Column(name = "student_id")
    private String studentId;

    private String name;

    // Maps to the department_short column
    private String departmentShort;

    private Integer semester;

    // Maps to the photo_url column
    private String photoUrl;

    // --- NEW FIELD FOR EMERGENCY PASS VERIFICATION ---
    @Column(name = "parent_mobile")
    private String parentMobile;
}