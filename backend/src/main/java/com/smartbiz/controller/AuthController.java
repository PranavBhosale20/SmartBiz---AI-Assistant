package com.smartbiz.controller;

import com.smartbiz.dto.*;
import com.smartbiz.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<AuthResponseDTO> staffRegister(@Valid @RequestBody StaffRegisterDTO dto) {
        return ResponseEntity.ok(authService.registerStaff(dto));
    }

    @PostMapping("/patient-register")
    public ResponseEntity<AuthResponseDTO> patientRegister(@Valid @RequestBody PatientRegisterDTO dto) {
        return ResponseEntity.ok(authService.registerPatient(dto));
    }

    @PostMapping("/staff-login")
    public ResponseEntity<AuthResponseDTO> staffLogin(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.loginStaff(dto));
    }

    @PostMapping("/patient-login")
    public ResponseEntity<AuthResponseDTO> patientLogin(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.loginPatient(dto));
    }
}