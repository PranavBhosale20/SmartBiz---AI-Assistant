package com.smartbiz.dto;

import com.smartbiz.model.BloodGroup;
import com.smartbiz.model.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class UserRequestDTO {

    /* ==========================================================
       BASIC INFORMATION
    ========================================================== */

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z ]+$",
            message = "Name must contain only letters and spaces"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phone;

    /* ==========================================================
       PERSONAL INFORMATION
    ========================================================== */

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    /* ==========================================================
       MEDICAL INFORMATION
    ========================================================== */

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Emergency contact must contain exactly 10 digits"
    )
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