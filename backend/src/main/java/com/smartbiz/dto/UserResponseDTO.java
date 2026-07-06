package com.smartbiz.dto;

import com.smartbiz.model.Gender;
import java.time.LocalDateTime;

public class UserResponseDTO {

    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private Gender gender;
    private String role;
    private String profileImage;
    private LocalDateTime createdAt;

    public UserResponseDTO(Long id, String name, String username, String email,
                            String phone, Gender gender, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.role = "PATIENT";
        this.profileImage = null;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Gender getGender() { return gender; }
    public String getRole() { return role; }
    public String getProfileImage() { return profileImage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}