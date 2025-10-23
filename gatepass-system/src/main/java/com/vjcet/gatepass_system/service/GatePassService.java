package com.vjcet.gatepass_system.service;

import com.vjcet.gatepass_system.model.GatePass;
import com.vjcet.gatepass_system.repository.GatePassRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer responsible for all business logic related to Gate Pass management.
 */
@Service
public class GatePassService {

    @Autowired
    private GatePassRepository gatePassRepository; // <-- This is the correct name

    /**
     * Submits a new pass request from a student.
     */
    public GatePass submitPassRequest(GatePass pass) {
        pass.setStatus("Pending");
        pass.setSubmittedAt(LocalDateTime.now());
        pass.setQrCodeData(null);

        return gatePassRepository.save(pass);
    }

    /**
     * Retrieves all pass requests for a specific student, sorted by submission date.
     */
    public List<GatePass> getPassHistory(String studentId) {
        return gatePassRepository.findByStudentIdOrderBySubmittedAtDesc(studentId);
    }

    /** Retrieves all passes awaiting HOD review. */
    public List<GatePass> getPendingPasses() {
        return gatePassRepository.findByStatus("Pending");
    }

    /** Retrieves all approved passes for HOD history. */
    public List<GatePass> getApprovedPasses() {
        return gatePassRepository.findByStatus("Approved");
    }

    /** Retrieves all rejected passes for HOD history. */
    public List<GatePass> getRejectedPasses() {
        return gatePassRepository.findByStatus("Rejected");
    }

    /**
     * HOD action: Approves a pending pass and generates a unique QR code ID.
     */
    @Transactional
    public Optional<GatePass> approvePass(Long passId) {
        Optional<GatePass> passOpt = gatePassRepository.findById(passId);

        if (passOpt.isPresent() && passOpt.get().getStatus().equals("Pending")) {
            GatePass pass = passOpt.get();
            pass.setStatus("Approved");
            pass.setHodApprovedDate(LocalDateTime.now());

            // Generate a unique, secure ID for the QR Code payload
            pass.setQrCodeData(UUID.randomUUID().toString());

            return Optional.of(gatePassRepository.save(pass));
        }
        return Optional.empty();
    }

    /**
     * HOD action: Rejects a pending pass and records the reason.
     */
    @Transactional
    public Optional<GatePass> rejectPass(Long passId, String reason) {
        Optional<GatePass> passOpt = gatePassRepository.findById(passId);

        if (passOpt.isPresent() && passOpt.get().getStatus().equals("Pending")) {
            GatePass pass = passOpt.get();
            pass.setStatus("Rejected");
            pass.setRejectionReason(reason);

            return Optional.of(gatePassRepository.save(pass));
        }
        return Optional.empty();
    }

    /**
     * Security action: Records the actual time the student checks out of the campus gate.
     */
    @Transactional
    public Optional<GatePass> recordCheckoutTime(Long passId) {
        // FIX: Changed 'passRepository' to the correctly defined 'gatePassRepository'
        Optional<GatePass> passOpt = gatePassRepository.findById(passId);

        if (passOpt.isPresent() && passOpt.get().getStatus().equals("Approved")) {
            GatePass pass = passOpt.get();

            // Set status to "Out"
            pass.setStatus("Out");

            // Log the current time as the exit time.
            pass.setDepartureTime(java.time.LocalTime.now());

            return Optional.of(gatePassRepository.save(pass));
        }
        return Optional.empty(); // Fails if not "Approved" or not found
    }
}