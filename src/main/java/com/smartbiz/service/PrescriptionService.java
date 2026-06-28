package com.smartbiz.service;

import com.smartbiz.dto.PrescriptionMapper;
import com.smartbiz.dto.PrescriptionRequestDTO;
import com.smartbiz.dto.PrescriptionResponseDTO;
import com.smartbiz.model.BillItem;
import com.smartbiz.model.Prescription;
import com.smartbiz.model.Product;
import com.smartbiz.repository.BillItemRepository;
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

    // NEW: needed so we can auto-create a BillItem alongside the
    // Prescription, in the same transaction.
    private final BillItemRepository billItemRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                                ProductRepository productRepository,
                                PrescriptionMapper prescriptionMapper,
                                BillItemRepository billItemRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.productRepository = productRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.billItemRepository = billItemRepository;
    }

    // @Transactional now covers THREE database writes instead of two:
    // 1) save the Prescription, 2) deduct stock, 3) create the
    // matching BillItem. All three succeed together, or all three
    // roll back together - there's no scenario where a prescription
    // exists without its bill item, or stock gets deducted without
    // either of those being recorded.
    @Transactional
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + dto.getProductId()));

        // --- Business Rule: can't prescribe more than what's in stock ---
        if (dto.getQuantityPrescribed() > product.getQuantity()) {
            throw new RuntimeException(
                    "Not enough stock! Available: " + product.getQuantity()
                    + ", Requested: " + dto.getQuantityPrescribed());
        }

        // Step 1: save the prescription itself. toEntity() resolves
        // appointmentId -> real Appointment, productId -> real Product.
        Prescription prescription = prescriptionMapper.toEntity(dto);
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Step 2: deduct stock.
        product.setQuantity(product.getQuantity() - dto.getQuantityPrescribed());
        productRepository.save(product);

        // Step 3: automatically create the matching BillItem - this
        // is the new piece. We capture the product's CURRENT price
        // right now, at the moment of sale, into unitPriceAtTimeOfSale -
        // this protects future bills from being affected if the
        // product's price changes later.
        BillItem billItem = new BillItem();
        billItem.setAppointment(savedPrescription.getAppointment());
        billItem.setProduct(product);
        billItem.setQuantity(dto.getQuantityPrescribed());
        billItem.setUnitPriceAtTimeOfSale(product.getPrice());
        billItem.setSubtotal(product.getPrice() * dto.getQuantityPrescribed());
        billItemRepository.save(billItem);

        return prescriptionMapper.toResponseDTO(savedPrescription);
    }

    public List<PrescriptionResponseDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(prescriptionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}