package com.smartbiz.dto;

// What every successful login/register returns - just the token and
// a couple of identifying fields the client will likely want
// immediately (no need to decode the JWT client-side just to show
// "Welcome, X").
public class AuthResponseDTO {
    private String token;
    private String username;
    private String role;

    public AuthResponseDTO(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}