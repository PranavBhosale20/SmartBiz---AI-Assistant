package com.smartbiz.service;

import com.smartbiz.dto.VisitTypeRequestDTO;
import com.smartbiz.dto.VisitTypeResponseDTO;
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

    public VisitTypeResponseDTO addVisitType(VisitTypeRequestDTO dto) {
        VisitType visitType = new VisitType();
        visitType.setName(dto.getName());
        visitType.setRepeatPrice(dto.getRepeatPrice());
        VisitType saved = visitTypeRepository.save(visitType);
        return toResponseDTO(saved);
    }

    public List<VisitTypeResponseDTO> getAllVisitTypes() {
        return visitTypeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private VisitTypeResponseDTO toResponseDTO(VisitType visitType) {
        return new VisitTypeResponseDTO(
                visitType.getId(),
                visitType.getName(),
                visitType.getRepeatPrice(),
                visitType.getCreatedAt()
        );
    }
}