package com.vjcet.gatepass_system.repository;

import com.vjcet.gatepass_system.model.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for SecurityLog entities.
 * Used to save security events (VERIFIED, CHECKED_OUT) and retrieve the checkout history.
 */
@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {

    /**
     * Retrieves all log records of a specific action type (e.g., "CHECKED_OUT"),
     * ordered by the most recent log time.
     * * Spring Data JPA automatically generates the SQL query based on the method name.
     * * @param actionType The action type (e.g., "CHECKED_OUT").
     * @return A list of matching SecurityLog entries.
     */
    List<SecurityLog> findByActionTypeOrderByLogTimestampDesc(String actionType);
}