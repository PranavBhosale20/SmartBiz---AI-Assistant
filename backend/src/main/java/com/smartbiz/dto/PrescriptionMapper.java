package com.smartbiz.dto;

import com.smartbiz.model.Appointment;
import com.smartbiz.model.Prescription;
import com.smartbiz.model.Product;
import org.springframework.stereotype.Component;

/**
 * CHANGED (Phase 5): toEntity() used to take just the
 * PrescriptionRequestDTO and look up the Appointment and Product
 * itself via repositories it held, throwing RuntimeException if
 * either id didn't exist. Same issue as AppointmentMapper had - a
 * mapper doing repository lookups isn't really a mapper's job.
 *
 * Now toEntity() takes the already-resolved Appointment and Product
 * directly. PrescriptionService resolves them first (throwing
 * ResourceNotFoundException if missing) before calling this method.
 * This class no longer needs AppointmentRepository/ProductRepository
 * injected at all - zero database dependency, zero exceptions.
 */
@Component
public class PrescriptionMapper {

    public Prescription toEntity(PrescriptionRequestDTO dto, Appointment appointment, Product product) {
        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setProduct(product);
        prescription.setQuantityPrescribed(dto.getQuantityPrescribed());
        prescription.setDosageInstructions(dto.getDosageInstructions());
        return prescription;
    }

    public PrescriptionResponseDTO toResponseDTO(Prescription prescription) {
        return new PrescriptionResponseDTO(
                prescription.getId(),
                prescription.getAppointment().getId(),
                // Pulled from the linked Appointment, not stored
                // directly on Prescription - this is exactly the
                // benefit of going through the relationship.
                prescription.getAppointment().getUser().getName(),
                prescription.getAppointment().getDoctor().getName(),
                prescription.getProduct().getId(),
                prescription.getProduct().getName(),
                prescription.getQuantityPrescribed(),
                prescription.getDosageInstructions(),
                prescription.getCreatedAt()
        );
    }
}