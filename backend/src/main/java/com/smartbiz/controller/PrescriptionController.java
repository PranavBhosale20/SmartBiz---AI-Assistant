package com.smartbiz.controller;

import com.smartbiz.dto.PrescriptionRequestDTO;
import com.smartbiz.dto.PrescriptionResponseDTO;
import com.smartbiz.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionResponseDTO> createPrescription(@RequestBody PrescriptionRequestDTO dto) {
        return ResponseEntity.ok(prescriptionService.createPrescription(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionResponseDTO>> getAllPrescriptions() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }
}