package com.vjcet.gatepass_system.repository;

import com.vjcet.gatepass_system.model.GatePass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for GatePass entities.
 * Includes custom queries for retrieving passes based on status and student ID.
 */
@Repository
public interface GatePassRepository extends JpaRepository<GatePass, Long> {

    // Student: Get all passes for a specific student, ordered by submission date (descending)
    List<GatePass> findByStudentIdOrderBySubmittedAtDesc(String studentId);

    // HOD: Get passes based on their status ('Pending', 'Approved', 'Rejected')
    List<GatePass> findByStatus(String status);

    /**
     * Security: Finds an approved pass by its unique QR code data.
     * This is used by the security portal for verification.
     * @param qrCodeData The unique ID generated upon approval.
     * @param status Must be "Approved".
     * @return The GatePass object if valid and approved.
     */
    Optional<GatePass> findByQrCodeDataAndStatus(String qrCodeData, String status);
}