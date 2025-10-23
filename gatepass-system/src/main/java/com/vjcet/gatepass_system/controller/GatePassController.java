package com.vjcet.gatepass_system.controller;

import com.vjcet.gatepass_system.model.GatePass;
import com.vjcet.gatepass_system.model.LoginRequest; // Used as DTO for Rejection Reason
import com.vjcet.gatepass_system.model.SecurityLog;
import com.vjcet.gatepass_system.repository.SecurityLogRepository;
import com.vjcet.gatepass_system.service.GatePassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles all API endpoints related to gate pass requests (Submission, HOD Approval, History).
 * @CrossOrigin fixes the persistent connection errors from the browser running on port 8000.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:8000", "http://127.0.0.1:8000", "file:///"}, allowCredentials = "true")
public class GatePassController {

    @Autowired
    private GatePassService gatePassService;

    // --- DEPENDENCY INJECTION FOR CHECKOUT LOG ---
    @Autowired
    private SecurityLogRepository securityLogRepository;


    // --- Student Endpoints ---

    /** Submits a new pass request from a student. */
    @PostMapping("/passes")
    public ResponseEntity<GatePass> submitPass(@RequestBody GatePass pass) {
        GatePass newPass = gatePassService.submitPassRequest(pass);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPass);
    }

    /** Fetches a student's entire pass history. */
    @GetMapping("/passes/history/{studentId}")
    public List<GatePass> getHistory(@PathVariable String studentId) {
        return gatePassService.getPassHistory(studentId);
    }

    // --- HOD Review/History Endpoints ---

    /** Fetches all pending passes for HOD review. */
    @GetMapping("/hod/pending")
    public List<GatePass> getHodPendingPasses() {
        return gatePassService.getPendingPasses();
    }

    /** Fetches all approved passes history. */
    @GetMapping("/hod/approved")
    public List<GatePass> getHodApprovedPasses() {
        return gatePassService.getApprovedPasses();
    }

    /** Fetches all rejected passes history. */
    @GetMapping("/hod/rejected")
    public List<GatePass> getHodRejectedPasses() {
        return gatePassService.getRejectedPasses();
    }

    /**
     * Approves a pass request and generates the QR code data.
     */
    @PostMapping("/hod/approve/{passId}")
    public ResponseEntity<?> approvePass(@PathVariable Long passId) {
        Optional<GatePass> updatedPass = gatePassService.approvePass(passId);

        return updatedPass.map(pass -> ResponseEntity.ok(Map.of("message", "Pass approved. QR Code Data generated.")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Pass not found or already processed.")));
    }

    /**
     * Rejects a pass request and records the reason.
     */
    @PostMapping("/hod/reject/{passId}")
    public ResponseEntity<?> rejectPass(@PathVariable Long passId, @RequestBody LoginRequest rejectionRequest) {
        // Calling the service method with the reason (reason is mapped to 'username' field of DTO)
        Optional<GatePass> updatedPass = gatePassService.rejectPass(passId, rejectionRequest.getUsername());

        return updatedPass.map(pass -> ResponseEntity.ok(Map.of("message", "Pass rejected.")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Pass not found or already processed.")));
    }

    /** * Fetches all successful CHECKOUT records for HOD reporting (from Security Logs).
     */
    @GetMapping("/hod/checkouts")
    public List<SecurityLog> getCheckoutHistory() {
        // Calls the method defined in SecurityLogRepository.java to filter by CHECKED_OUT action type.
        return securityLogRepository.findByActionTypeOrderByLogTimestampDesc("CHECKED_OUT");
    }
}