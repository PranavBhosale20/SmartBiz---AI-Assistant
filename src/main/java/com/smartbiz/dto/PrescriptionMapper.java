package com.smartbiz.dto;

import com.smartbiz.model.Doctor;
import com.smartbiz.model.Prescription;
import com.smartbiz.model.Product;
import com.smartbiz.model.User;
import com.smartbiz.repository.DoctorRepository;
import com.smartbiz.repository.ProductRepository;
import com.smartbiz.repository.UserRepository;
import org.springframework.stereotype.Component;

// This mapper needs to resolve THREE different ids (userId, doctorId,
// productId) into real entities - more than any mapper we've written
// so far, but the same underlying pattern as AppointmentMapper, just
// with one extra relationship.
@Component
public class PrescriptionMapper {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ProductRepository productRepository;

    public PrescriptionMapper(UserRepository userRepository,
                               DoctorRepository doctorRepository,
                               ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.productRepository = productRepository;
    }

    public Prescription toEntity(PrescriptionRequestDTO dto) {
        Prescription prescription = new Prescription();

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + dto.getDoctorId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProductId()));

        prescription.setUser(user);
        prescription.setDoctor(doctor);
        prescription.setProduct(product);
        prescription.setQuantityPrescribed(dto.getQuantityPrescribed());
        prescription.setDosageInstructions(dto.getDosageInstructions());

        return prescription;
    }

    public PrescriptionResponseDTO toResponseDTO(Prescription prescription) {
        return new PrescriptionResponseDTO(
                prescription.getId(),
                prescription.getUser().getId(),
                prescription.getUser().getName(),
                prescription.getDoctor().getId(),
                prescription.getDoctor().getName(),
                prescription.getProduct().getId(),
                prescription.getProduct().getName(),
                prescription.getQuantityPrescribed(),
                prescription.getDosageInstructions(),
                prescription.getCreatedAt()
        );
    }
}