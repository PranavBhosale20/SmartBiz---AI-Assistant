package com.smartbiz.service;

import com.smartbiz.dto.PrescriptionItemDTO;
import com.smartbiz.dto.PrescriptionMapper;
import com.smartbiz.dto.PrescriptionRequestDTO;
import com.smartbiz.dto.PrescriptionResponseDTO;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.*;
import com.smartbiz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final AppointmentRepository appointmentRepository;
    private final ProductRepository productRepository;
    private final BillItemRepository billItemRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                                PrescriptionMapper prescriptionMapper,
                                AppointmentRepository appointmentRepository,
                                ProductRepository productRepository,
                                BillItemRepository billItemRepository,
                                PrescriptionItemRepository prescriptionItemRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.appointmentRepository = appointmentRepository;
        this.productRepository = productRepository;
        this.billItemRepository = billItemRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
    }

    @Transactional
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", dto.getAppointmentId()));

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("Prescription must contain at least one medicine.");
        }

        // Step 1: save prescription header
        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setNotes(dto.getNotes());
        Prescription savedPrescription = prescriptionRepository.save(prescription);

        // Step 2: process each medicine item
        List<PrescriptionItem> savedItems = new ArrayList<>();
        for (PrescriptionItemDTO itemDTO : dto.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemDTO.getProductId()));

            if (itemDTO.getQuantityPrescribed() > product.getQuantity()) {
                throw new BusinessException(
                        "Not enough stock for " + product.getName() +
                        "! Available: " + product.getQuantity() +
                        ", Requested: " + itemDTO.getQuantityPrescribed());
            }

            // Save prescription item
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(savedPrescription);
            item.setProduct(product);
            item.setQuantityPrescribed(itemDTO.getQuantityPrescribed());
            item.setDosageInstructions(itemDTO.getDosageInstructions());
            savedItems.add(prescriptionItemRepository.save(item));

            // Deduct stock
            product.setQuantity(product.getQuantity() - itemDTO.getQuantityPrescribed());
            productRepository.save(product);

            // Auto-create BillItem for this medicine
            BillItem billItem = new BillItem();
            billItem.setAppointment(appointment);
            billItem.setProduct(product);
            billItem.setQuantity(itemDTO.getQuantityPrescribed());
            billItem.setUnitPriceAtTimeOfSale(product.getPrice());
            billItem.setSubtotal(product.getPrice() * itemDTO.getQuantityPrescribed());
            billItemRepository.save(billItem);
        }

        savedPrescription.setItems(savedItems);
        return prescriptionMapper.toResponseDTO(savedPrescription);
    }

    public List<PrescriptionResponseDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(prescriptionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    /* ==========================================================
    GET BY ID
 ========================================================== */

 public PrescriptionResponseDTO getPrescriptionById(Long id) {

     Prescription prescription = prescriptionRepository.findById(id)
             .orElseThrow(() ->
                     new ResourceNotFoundException(
                             "Prescription",
                             id));

     return prescriptionMapper.toResponseDTO(prescription);
 }

}