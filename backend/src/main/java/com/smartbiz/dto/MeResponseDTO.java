package com.smartbiz.dto;

import com.smartbiz.model.Gender;

public class MeResponseDTO {

    private Long id;
    private String name;
    private String username;
    private String role;
    private String email;
    private Gender gender;
    private String profileImage;

    public MeResponseDTO(Long id, String name, String username,
                          String role, String email, Gender gender) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
        this.email = email;
        this.gender = gender;
        this.profileImage = null;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public Gender getGender() { return gender; }
    public String getProfileImage() { return profileImage; }
}