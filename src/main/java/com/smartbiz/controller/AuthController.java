package com.smartbiz.controller;

import com.smartbiz.dto.*;
import com.smartbiz.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/staff-register")
    public ResponseEntity<AuthResponseDTO> staffRegister(@RequestBody StaffRegisterDTO dto) {
        return ResponseEntity.ok(authService.registerStaff(dto));
    }

    @PostMapping("/patient-register")
    public ResponseEntity<AuthResponseDTO> patientRegister(@RequestBody PatientRegisterDTO dto) {
        return ResponseEntity.ok(authService.registerPatient(dto));
    }

    @PostMapping("/staff-login")
    public ResponseEntity<AuthResponseDTO> staffLogin(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.loginStaff(dto));
    }

    @PostMapping("/patient-login")
    public ResponseEntity<AuthResponseDTO> patientLogin(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.loginPatient(dto));
    }
}