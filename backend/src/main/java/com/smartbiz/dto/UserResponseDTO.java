package com.smartbiz.dto;

import com.smartbiz.model.BloodGroup;
import com.smartbiz.model.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserResponseDTO {

    /* ==========================================================
       BASIC INFORMATION
    ========================================================== */

    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;

    /* ==========================================================
       PERSONAL INFORMATION
    ========================================================== */

    private Gender gender;
    private LocalDate dateOfBirth;
    private String address;

    /* ==========================================================
       MEDICAL INFORMATION
    ========================================================== */

    private BloodGroup bloodGroup;
    private String emergencyContact;

    /* ==========================================================
       SYSTEM INFORMATION
    ========================================================== */

    private String role;
    private String profileImage;
    private LocalDateTime createdAt;

    public UserResponseDTO(
            Long id,
            String name,
            String username,
            String email,
            String phone,
            Gender gender,
            LocalDate dateOfBirth,
            String address,
            BloodGroup bloodGroup,
            String emergencyContact,
            String profileImage,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.role = "PATIENT";
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }

    /* ==========================================================
       GETTERS
    ========================================================== */

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public String getRole() {
        return role;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}