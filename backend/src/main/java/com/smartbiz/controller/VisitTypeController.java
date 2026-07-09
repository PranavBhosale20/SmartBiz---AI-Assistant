package com.smartbiz.controller;

import com.smartbiz.dto.VisitTypeRequestDTO;
import com.smartbiz.dto.VisitTypeResponseDTO;
import com.smartbiz.service.VisitTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visit-types")
public class VisitTypeController {

    private final VisitTypeService visitTypeService;

    public VisitTypeController(VisitTypeService visitTypeService) {
        this.visitTypeService = visitTypeService;
    }

    /* ==========================================================
       CREATE
    ========================================================== */

    @PostMapping
    public ResponseEntity<VisitTypeResponseDTO> addVisitType(
            @RequestBody VisitTypeRequestDTO dto) {

        return ResponseEntity.ok(
                visitTypeService.addVisitType(dto));
    }

    /* ==========================================================
       GET ALL
    ========================================================== */

    @GetMapping
    public ResponseEntity<List<VisitTypeResponseDTO>> getAllVisitTypes() {

        return ResponseEntity.ok(
                visitTypeService.getAllVisitTypes());
    }

    /* ==========================================================
       GET BY ID
    ========================================================== */

    @GetMapping("/{id}")
    public ResponseEntity<VisitTypeResponseDTO> getVisitTypeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                visitTypeService.getVisitTypeById(id));
    }

    /* ==========================================================
       UPDATE
    ========================================================== */

    @PutMapping("/{id}")
    public ResponseEntity<VisitTypeResponseDTO> updateVisitType(
            @PathVariable Long id,
            @RequestBody VisitTypeRequestDTO dto) {

        return ResponseEntity.ok(
                visitTypeService.updateVisitType(id, dto));
    }

    /* ==========================================================
       DELETE
    ========================================================== */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitType(
            @PathVariable Long id) {

        visitTypeService.deleteVisitType(id);

        return ResponseEntity.noContent().build();
    }
}