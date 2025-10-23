package com.vjcet.gatepass_system.controller;

import com.vjcet.gatepass_system.model.GatePass;
import com.vjcet.gatepass_system.model.Student;
import com.vjcet.gatepass_system.repository.StudentRepository;
import com.vjcet.gatepass_system.service.GatePassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

// Twilio Imports for Live SMS
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

// DTO to receive emergency pass request data (Required for JSON mapping)
class EmergencyRequest {
    private String studentId;
    private String studentName;
    private String purpose;
    private String parentMobileInput;

    // Manual Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getParentMobileInput() { return parentMobileInput; }
    public void setParentMobileInput(String parentMobileInput) { this.parentMobileInput = parentMobileInput; }
}

/**
 * Handles the Emergency Gate Pass workflow.
 * This includes Guardian mobile verification and triggering a live SMS via Twilio.
 */
@RestController
@RequestMapping("/api/emergency")
@CrossOrigin(origins = {"http://localhost:8000", "http://127.0.0.1:8000", "file:///"}, allowCredentials = "true")
public class EmergencyController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GatePassService gatePassService;

    // --- TWILIO CREDENTIALS - REPLACE THESE PLACEHOLDERS ---
    // You MUST update these with the values from your Twilio Console.
    final String ACCOUNT_SID = "ACf8977d398ab6a1a6cfeef12466a11654";
    final String AUTH_TOKEN = "70bf1986c9880e63c9b4c9ab96468c7c";
    final String TWILIO_PHONE_NUMBER = "+14344783921"; // e.g., +15017122661
    // ------------------------------------------------------


    @PostMapping("/submit")
    @Transactional
    public ResponseEntity<Map<String, Object>> submitEmergencyPass(@RequestBody EmergencyRequest request) {

        Optional<Student> studentOpt = studentRepository.findById(request.getStudentId());

        // 1. Student ID Check
        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Invalid Student ID provided."));
        }

        Student student = studentOpt.get();

        // Input validation
        if (student.getParentMobile() == null || student.getParentMobile().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Guardian contact information is missing in system records. Pass rejected."));
        }

        // Clean mobile numbers for robust comparison
        String cleanedInputMobile = request.getParentMobileInput().replaceAll("[^0-9]", "");
        String cleanedDbMobile = student.getParentMobile().replaceAll("[^0-9]", "");

        // 2. Parent Mobile Match Check
        if (!cleanedDbMobile.equals(cleanedInputMobile)) {
            // AUTOMATIC REJECT DUE TO MOBILE MISMATCH
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Guardian mobile number mismatch. Pass rejected."));
        }

        // --- SUCCESS: AUTOMATIC APPROVAL AND SMS TRIGGER ---

        // 3. Create and Approve Pass
        GatePass pass = new GatePass();
        pass.setStudentId(student.getStudentId());
        pass.setStudentName(student.getName()); // Ensure name is saved
        pass.setPurpose("EMERGENCY: " + request.getPurpose());
        pass.setDepartureDate(java.time.LocalDate.now());
        pass.setDepartureTime(java.time.LocalTime.now());

        GatePass pendingPass = gatePassService.submitPassRequest(pass);
        Optional<GatePass> approvedPassOpt = gatePassService.approvePass(pendingPass.getPassId());

        if (approvedPassOpt.isPresent()) {
            GatePass approvedPass = approvedPassOpt.get();

            // 4. LIVE SMS SEND (Twilio)
            String recipientNumber = "+91" + cleanedDbMobile; // Format for India (E.164)
            String reason = request.getPurpose();

            String messageBody = String.format(
                    "VJCET EMERGENCY: Pass Approved for %s (%s). Reason: %s. Visit the Security Desk with QR Code. - VJCET (9497838460)",
                    student.getName(), student.getStudentId(), reason
            );

            try {
                // Initialize Twilio client and send message
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

                Message message = Message.creator(
                        new PhoneNumber(recipientNumber),
                        new PhoneNumber(TWILIO_PHONE_NUMBER),
                        messageBody
                ).create();

                System.out.println("--- LIVE TWILIO SMS SENT ---");
                System.out.println("Recipient: " + recipientNumber + " SID: " + message.getSid());

            } catch (Exception e) {
                System.err.println("--- TWILIO SMS FAILED (CHECK CREDENTIALS/BALANCE/E.164 FORMAT) ---");
                System.err.println("Error: " + e.getMessage());
                // Pass generation is successful despite SMS failure.
            }

            // 5. Return Approved Pass Data
            return ResponseEntity.ok(Map.of(
                    "message", "Approval successful. QR Code generated. Guardian notified.",
                    "qrCodeData", approvedPass.getQrCodeData()
            ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error finalizing pass approval."));
    }
}