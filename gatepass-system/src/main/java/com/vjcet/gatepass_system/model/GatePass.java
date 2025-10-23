package com.vjcet.gatepass_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * Database Entity representing the 'gate_passes' table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
// JPA annotation mapping this class to the 'gate_passes' table
@Entity
@Table(name = "gate_passes")
public class GatePass {

    // Primary Key: Auto-generated ID in the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passId;

    // Foreign Key: Links to the student who submitted the pass
    private String studentId;

    // --- NEW FIELD TO STORE NAME DIRECTLY ---
    // This is the fix for the HOD Dashboard display issue.
    private String studentName;

    // Request details
    private String purpose;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate returnDate;
    private LocalTime returnTime;

    // Status and approval workflow
    private String status; // Pending, Approved, Rejected
    private LocalDateTime submittedAt;
    private LocalDateTime hodApprovedDate;
    private String rejectionReason;

    // Unique code used for security verification (QR code payload)
    @Column(unique = true)
    private String qrCodeData;
}