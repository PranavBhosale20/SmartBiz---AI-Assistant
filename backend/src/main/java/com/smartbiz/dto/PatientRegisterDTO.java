package com.smartbiz.dto;

import com.smartbiz.model.BloodGroup;
import com.smartbiz.model.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class PatientRegisterDTO {

    /* ==========================================================
       BASIC INFORMATION
    ========================================================== */

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 50,
            message = "Full name must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z ]+$",
            message = "Full name must contain only letters and spaces")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits")
    private String phone;

    /* ==========================================================
       LOGIN INFORMATION
    ========================================================== */

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20,
            message = "Username must be between 4 and 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username must contain only letters, numbers and underscore")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8,
            message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!*()_\\-]).+$",
            message = "Password must contain at least 1 uppercase, 1 lowercase, 1 digit and 1 special character")
    private String password;

    /* ==========================================================
       PERSONAL INFORMATION
    ========================================================== */

    @NotNull(message = "Gender is required")
    private Gender gender;

    private LocalDate dateOfBirth;

    @Size(max = 500,
            message = "Address cannot exceed 500 characters")
    private String address;

    /* ==========================================================
       MEDICAL INFORMATION
    ========================================================== */

    private BloodGroup bloodGroup;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Emergency contact must contain exactly 10 digits")
    private String emergencyContact;

    /* ==========================================================
       GETTERS & SETTERS
    ========================================================== */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}