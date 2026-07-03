package com.smartbiz.controller;

import com.smartbiz.dto.BillItemRequestDTO;
import com.smartbiz.dto.BillItemResponseDTO;
import com.smartbiz.service.BillItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bill-items")
public class BillItemController {

    private final BillItemService billItemService;

    public BillItemController(BillItemService billItemService) {
        this.billItemService = billItemService;
    }

    @PostMapping
    public ResponseEntity<BillItemResponseDTO> addBillItem(@RequestBody BillItemRequestDTO dto) {
        return ResponseEntity.ok(billItemService.addBillItem(dto));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<BillItemResponseDTO>> getBillItemsByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billItemService.getBillItemsByAppointmentId(appointmentId));
    }
}