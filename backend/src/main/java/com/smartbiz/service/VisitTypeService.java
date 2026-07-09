package com.smartbiz.service;

import com.smartbiz.dto.VisitTypeRequestDTO;
import com.smartbiz.dto.VisitTypeResponseDTO;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.VisitType;
import com.smartbiz.repository.VisitTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitTypeService {

    private final VisitTypeRepository visitTypeRepository;

    public VisitTypeService(VisitTypeRepository visitTypeRepository) {
        this.visitTypeRepository = visitTypeRepository;
    }

    /* ==========================================================
       CREATE
    ========================================================== */

    public VisitTypeResponseDTO addVisitType(VisitTypeRequestDTO dto) {

        visitTypeRepository.findByName(dto.getName())
                .ifPresent(v -> {
                    throw new BusinessException(
                            "Visit Type already exists.");
                });

        VisitType visitType = new VisitType();

        visitType.setName(dto.getName());
        visitType.setFirstVisitPrice(dto.getFirstVisitPrice());
        visitType.setRepeatPrice(dto.getRepeatPrice());

        VisitType saved = visitTypeRepository.save(visitType);

        return toResponseDTO(saved);
    }

    /* ==========================================================
       GET ALL
    ========================================================== */

    public List<VisitTypeResponseDTO> getAllVisitTypes() {

        return visitTypeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /* ==========================================================
       GET BY ID
    ========================================================== */

    public VisitTypeResponseDTO getVisitTypeById(Long id) {

        VisitType visitType = visitTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit Type",
                                id));

        return toResponseDTO(visitType);
    }

    /* ==========================================================
       UPDATE
    ========================================================== */

    public VisitTypeResponseDTO updateVisitType(
            Long id,
            VisitTypeRequestDTO dto) {

        VisitType visitType = visitTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit Type",
                                id));

        visitType.setName(dto.getName());
        visitType.setFirstVisitPrice(dto.getFirstVisitPrice());
        visitType.setRepeatPrice(dto.getRepeatPrice());

        VisitType updated = visitTypeRepository.save(visitType);

        return toResponseDTO(updated);
    }

    /* ==========================================================
       DELETE
    ========================================================== */

    public void deleteVisitType(Long id) {

        VisitType visitType = visitTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visit Type",
                                id));

        visitTypeRepository.delete(visitType);
    }

    /* ==========================================================
       MAPPER
    ========================================================== */

    private VisitTypeResponseDTO toResponseDTO(
            VisitType visitType) {

        return new VisitTypeResponseDTO(
                visitType.getId(),
                visitType.getName(),
                visitType.getFirstVisitPrice(),
                visitType.getRepeatPrice(),
                visitType.getCreatedAt()
        );
    }
}