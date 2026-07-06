package com.smartbiz.controller;

import com.smartbiz.dto.DoctorRequestDTO;
import com.smartbiz.dto.DoctorResponseDTO;
import com.smartbiz.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /* ==========================================================
       CREATE DOCTOR
    ========================================================== */

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> addDoctor(
            @RequestBody DoctorRequestDTO dto) {

        return ResponseEntity.ok(doctorService.addDoctor(dto));
    }

    /* ==========================================================
       GET ALL DOCTORS
    ========================================================== */

    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {

        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    /* ==========================================================
       GET DOCTOR BY ID
    ========================================================== */

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(
            @PathVariable Long id) {

        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    /* ==========================================================
       UPDATE DOCTOR
    ========================================================== */

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable Long id,
            @RequestBody DoctorRequestDTO dto) {

        return ResponseEntity.ok(doctorService.updateDoctor(id, dto));
    }

    /* ==========================================================
       DELETE DOCTOR
    ========================================================== */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(
            @PathVariable Long id) {

        doctorService.deleteDoctor(id);

        return ResponseEntity.noContent().build();
    }
}