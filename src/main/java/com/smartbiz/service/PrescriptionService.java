package com.smartbiz.service;

import com.smartbiz.dto.PrescriptionMapper;
import com.smartbiz.dto.PrescriptionRequestDTO;
import com.smartbiz.dto.PrescriptionResponseDTO;
import com.smartbiz.model.Prescription;
import com.smartbiz.model.Product;
import com.smartbiz.repository.PrescriptionRepository;
import com.smartbiz.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ProductRepository productRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                                ProductRepository productRepository,
                                PrescriptionMapper prescriptionMapper) {
        this.prescriptionRepository = prescriptionRepository;
        this.productRepository = productRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    // @Transactional is the key piece here. It tells Spring: "treat
    // everything inside this method as ONE unit." If ANY line inside
    // here throws an exception, Spring automatically UNDOES every
    // database change this method made so far - it's all or nothing.
    // Without this annotation, if the prescription save succeeded but
    // the stock deduction failed afterward (for whatever reason), we'd
    // be left with a prescription on record for medicine that was
    // never actually removed from inventory - a silent, hard-to-debug
    // data bug. @Transactional makes that scenario impossible.
    @Transactional
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO dto) {

        // Fetch the product FIRST, before saving anything, so we can
        // check stock availability before committing to this
        // prescription at all.
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProductId()));

        // --- Business Rule: can't prescribe more than what's in stock ---
        if (dto.getQuantityPrescribed() > product.getQuantity()) {
            throw new RuntimeException(
                    "Not enough stock! Available: " + product.getQuantity()
                    + ", Requested: " + dto.getQuantityPrescribed());
        }

        // Step 1: build and save the prescription itself.
        Prescription prescription = prescriptionMapper.toEntity(dto);
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Step 2: deduct the prescribed quantity from the product's
        // stock, and save THAT change too. If this line were to fail
        // for any reason, @Transactional ensures the prescription
        // saved in Step 1 gets rolled back too - they succeed or fail
        // together, never just one of them.
        product.setQuantity(product.getQuantity() - dto.getQuantityPrescribed());
        productRepository.save(product);

        return prescriptionMapper.toResponseDTO(savedPrescription);
    }

    public List<PrescriptionResponseDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(prescriptionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PrescriptionResponseDTO> getPrescriptionsByUserId(Long userId) {
        return prescriptionRepository.findByUserId(userId)
                .stream()
                .map(prescriptionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}