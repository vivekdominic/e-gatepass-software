package com.vjcet.gatepass_system.controller;

import com.vjcet.gatepass_system.model.GatePass;
import com.vjcet.gatepass_system.model.Student;
import com.vjcet.gatepass_system.repository.GatePassRepository;
import com.vjcet.gatepass_system.repository.StudentRepository;
import com.vjcet.gatepass_system.service.GatePassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// DTO for security verification request body
class VerifyRequest {
    private String qrCodeData;

    // Manual getters/setters (required since this is not the main model DTO)
    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }
}

/**
 * Handles all API endpoints related to the Security Portal (Verification and Checkout).
 */
@RestController
@RequestMapping("/api")
// CORS Fix: Allows traffic from your local server (8000) or public host
@CrossOrigin(origins = {"http://localhost:8000", "http://127.0.0.1:8000", "file:///"}, allowCredentials = "true")
public class SecurityController {

    @Autowired
    private GatePassRepository gatePassRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GatePassService gatePassService;

    /**
     * Endpoint to verify a gate pass using its unique QR code data string.
     * Updated to include the pass purpose for the security guard display.
     */
    @PostMapping("/security/verify")
    public ResponseEntity<Map<String, Object>> verifyGatePass(@RequestBody VerifyRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 1. Look up the pass using the unique QR code data AND ensuring the status is "Approved"
        Optional<GatePass> passOpt = gatePassRepository.findByQrCodeDataAndStatus(request.getQrCodeData(), "Approved");

        if (passOpt.isPresent()) {
            GatePass pass = passOpt.get();

            // 2. Fetch the student details for confirmation display
            Optional<Student> studentOpt = studentRepository.findById(pass.getStudentId());

            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();

                // Success: Return detailed pass and student information
                response.put("status", "Approved");
                response.put("message", "Gate Pass is valid.");
                response.put("passId", pass.getPassId()); // CRITICAL: Send Pass ID back for the Checkout call
                response.put("name", student.getName());
                response.put("studentId", student.getStudentId());
                response.put("departureDate", pass.getDepartureDate());
                response.put("departureTime", pass.getDepartureTime());

                // --- CRITICAL ADDITION: Send the pass purpose back ---
                response.put("purpose", pass.getPurpose());

                return ResponseEntity.ok(response);
            }
        }

        // Failure: Code not found, not approved, or invalid
        response.put("status", "Invalid");
        response.put("message", "Code not found, rejected, or invalid.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Endpoint to record the actual checkout time after successful verification.
     * This is called when the security guard presses the "Record Checkout" button.
     */
    @PostMapping("/security/checkout/{passId}")
    public ResponseEntity<Map<String, Object>> checkoutPass(@PathVariable Long passId) {

        Optional<GatePass> passOpt = gatePassService.recordCheckoutTime(passId);

        if (passOpt.isPresent()) {
            return ResponseEntity.ok(Map.of("message", "Checkout recorded successfully. Student has left the campus.", "status", "success"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pass not found or not approved for checkout.", "status", "error"));
        }
    }
}