package com.vjcet.gatepass_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Database Entity representing the 'security_logs' table.
 * Stores records of verification and checkout actions at the college gate.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "security_logs")
public class SecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "pass_id")
    private Long passId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "action_type")
    private String actionType; // e.g., 'VERIFIED' or 'CHECKED_OUT'

    @Column(name = "log_timestamp")
    private LocalDateTime logTimestamp;

    // Custom constructor for easy logging from the service layer
    public SecurityLog(Long passId, String studentId, String studentName, String purpose, String actionType) {
        this.passId = passId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.purpose = purpose;
        this.actionType = actionType;
        this.logTimestamp = LocalDateTime.now();
    }
}