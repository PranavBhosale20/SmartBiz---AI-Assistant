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

    @PostMapping
    public ResponseEntity<VisitTypeResponseDTO> addVisitType(@RequestBody VisitTypeRequestDTO dto) {
        return ResponseEntity.ok(visitTypeService.addVisitType(dto));
    }

    @GetMapping
    public ResponseEntity<List<VisitTypeResponseDTO>> getAllVisitTypes() {
        return ResponseEntity.ok(visitTypeService.getAllVisitTypes());
    }
}