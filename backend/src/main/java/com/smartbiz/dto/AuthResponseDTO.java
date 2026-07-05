package com.smartbiz.dto;

import com.smartbiz.model.Gender;

public class AuthResponseDTO {
    private String token;
    private String username;
    private String fullName;
    private String role;
    private Gender gender;

    // Constructor for STAFF (no fullName, no gender)
    public AuthResponseDTO(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.fullName = null;
        this.gender = null;
    }

    // Constructor for PATIENT (with fullName and gender)
    public AuthResponseDTO(String token, String username, String fullName,
                            String role, Gender gender) {
        this.token = token;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.gender = gender;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public Gender getGender() { return gender; }
}