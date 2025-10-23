package com.vjcet.gatepass_system.repository;

import com.vjcet.gatepass_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * Handles database operations related to login credentials and user roles.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Custom method to find a user entity based on the username provided during login.
     * Spring Data JPA automatically generates the query for this method name.
     * @param username The username (e.g., student ID, 'cshod')
     * @return An Optional containing the User if found.
     */
    Optional<User> findByUsername(String username);
}