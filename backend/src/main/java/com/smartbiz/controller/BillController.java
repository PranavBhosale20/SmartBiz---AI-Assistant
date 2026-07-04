package com.smartbiz.controller;

import com.smartbiz.dto.BillResponseDTO;
import com.smartbiz.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/generate/{appointmentId}")
    public ResponseEntity<BillResponseDTO> generateBill(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billService.generateBill(appointmentId));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<BillResponseDTO> getBillByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billService.getBillByAppointmentId(appointmentId));
    }

    // NEW: marks a bill as PAID - called by receptionist after
    // receiving payment from patient.
    @PutMapping("/{id}/pay")
    public ResponseEntity<BillResponseDTO> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(billService.markAsPaid(id));
    }
}