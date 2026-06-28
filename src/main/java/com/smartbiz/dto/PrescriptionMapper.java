package com.smartbiz.dto;

import com.smartbiz.model.Appointment;
import com.smartbiz.model.Prescription;
import com.smartbiz.model.Product;
import com.smartbiz.repository.AppointmentRepository;
import com.smartbiz.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {

    private final AppointmentRepository appointmentRepository;
    private final ProductRepository productRepository;

    // CHANGED: no longer needs UserRepository or DoctorRepository
    // directly - just AppointmentRepository, since user and doctor
    // are both reachable through the Appointment we look up.
    public PrescriptionMapper(AppointmentRepository appointmentRepository,
                               ProductRepository productRepository) {
        this.appointmentRepository = appointmentRepository;
        this.productRepository = productRepository;
    }

    public Prescription toEntity(PrescriptionRequestDTO dto) {
        Prescription prescription = new Prescription();

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found with id: " + dto.getAppointmentId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + dto.getProductId()));

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