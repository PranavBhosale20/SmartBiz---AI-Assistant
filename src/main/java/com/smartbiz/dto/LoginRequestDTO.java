package com.smartbiz.dto;

// Shared by both staff-login and patient-login - same shape, just
// different endpoints decide which table to check against.
public class LoginRequestDTO {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}