package com.smartbiz.dto;

import com.smartbiz.model.Prescription;
import com.smartbiz.model.PrescriptionItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrescriptionMapper {

    public PrescriptionResponseDTO toResponseDTO(Prescription prescription) {
        List<PrescriptionItemResponseDTO> itemDTOs = prescription.getItems() == null
                ? List.of()
                : prescription.getItems().stream()
                        .map(item -> new PrescriptionItemResponseDTO(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantityPrescribed(),
                                item.getDosageInstructions()
                        ))
                        .collect(Collectors.toList());

        return new PrescriptionResponseDTO(
                prescription.getId(),
                prescription.getAppointment().getId(),
                prescription.getAppointment().getUser().getName(),
                prescription.getAppointment().getDoctor().getName(),
                prescription.getNotes(),
                itemDTOs,
                prescription.getCreatedAt()
        );
    }
}