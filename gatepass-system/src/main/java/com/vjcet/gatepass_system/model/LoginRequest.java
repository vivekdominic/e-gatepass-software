package com.vjcet.gatepass_system.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object (DTO) used to capture the JSON body
 * sent from the browser during the login request.
 * It carries the credentials and the selected role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    // Maps to the username field from the login form
    private String username;

    // Maps to the password field from the login form
    private String password;

    // Maps to the role field ('student', 'hod', etc.) from the login form
    private String role;
}