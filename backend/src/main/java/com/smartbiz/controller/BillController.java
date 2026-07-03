package com.smartbiz.controller;

import com.smartbiz.model.Bill;
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

    // POST /api/bills/generate/5 - generates (or returns the existing)
    // bill for appointment id 5.
    @PostMapping("/generate/{appointmentId}")
    public ResponseEntity<Bill> generateBill(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billService.generateBill(appointmentId));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Bill> getBillByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billService.getBillByAppointmentId(appointmentId));
    }
}