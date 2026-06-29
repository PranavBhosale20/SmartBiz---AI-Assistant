package com.smartbiz.service;

import com.smartbiz.dto.PrescriptionMapper;
import com.smartbiz.dto.PrescriptionRequestDTO;
import com.smartbiz.dto.PrescriptionResponseDTO;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.Appointment;
import com.smartbiz.model.BillItem;
import com.smartbiz.model.Prescription;
import com.smartbiz.model.Product;
import com.smartbiz.repository.AppointmentRepository;
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
    private final BillItemRepository billItemRepository;

    // NEW (Phase 5): AppointmentRepository moved here from
    // PrescriptionMapper - this Service now resolves the
    // appointmentId itself before handing the resolved Appointment
    // to the (now pure) mapper.
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                                ProductRepository productRepository,
                                PrescriptionMapper prescriptionMapper,
                                BillItemRepository billItemRepository,
                                AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.productRepository = productRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.billItemRepository = billItemRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO dto) {
        // NEW (Phase 5): resolving Appointment and Product here,
        // BEFORE calling the mapper - these lookups used to live
        // inside PrescriptionMapper.toEntity().
        // ResourceNotFoundException instead of RuntimeException -
        // both are genuine "doesn't exist" cases (404).
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", dto.getAppointmentId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", dto.getProductId()));

        // --- Business Rule: can't prescribe more than what's in stock ---
        // CHANGED (Phase 5): BusinessException instead of RuntimeException.
        if (dto.getQuantityPrescribed() > product.getQuantity()) {
            throw new BusinessException(
                    "Not enough stock! Available: " + product.getQuantity()
                            + ", Requested: " + dto.getQuantityPrescribed());
        }

        // Step 1: save the prescription itself. toEntity() now takes
        // the already-resolved appointment/product directly, instead
        // of resolving them itself.
        Prescription prescription = prescriptionMapper.toEntity(dto, appointment, product);
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Step 2: deduct stock.
        product.setQuantity(product.getQuantity() - dto.getQuantityPrescribed());
        productRepository.save(product);

        // Step 3: automatically create the matching BillItem.
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