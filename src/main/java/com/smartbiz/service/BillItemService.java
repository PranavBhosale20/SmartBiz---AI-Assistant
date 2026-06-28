package com.smartbiz.service;

import com.smartbiz.dto.BillItemRequestDTO;
import com.smartbiz.dto.BillItemResponseDTO;
import com.smartbiz.model.Appointment;
import com.smartbiz.model.BillItem;
import com.smartbiz.model.Product;
import com.smartbiz.repository.AppointmentRepository;
import com.smartbiz.repository.BillItemRepository;
import com.smartbiz.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillItemService {

    private final BillItemRepository billItemRepository;
    private final ProductRepository productRepository;
    private final AppointmentRepository appointmentRepository;

    public BillItemService(BillItemRepository billItemRepository,
                            ProductRepository productRepository,
                            AppointmentRepository appointmentRepository) {
        this.billItemRepository = billItemRepository;
        this.productRepository = productRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // This is for STAFF manually adding a non-prescribed extra (e.g.
    // sanitizer, bandaids) to a visit's bill. Same @Transactional
    // safety as PrescriptionService - stock deduction and bill item
    // creation succeed or fail together.
    @Transactional
    public BillItemResponseDTO addBillItem(BillItemRequestDTO dto) {

        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException(
                        "Appointment not found with id: " + dto.getAppointmentId()));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Product not found with id: " + dto.getProductId()));

        // Same stock-availability rule as prescriptions - can't sell
        // more than what's physically on the shelf.
        if (dto.getQuantity() > product.getQuantity()) {
            throw new RuntimeException(
                    "Not enough stock! Available: " + product.getQuantity()
                    + ", Requested: " + dto.getQuantity());
        }

        product.setQuantity(product.getQuantity() - dto.getQuantity());
        productRepository.save(product);

        BillItem billItem = new BillItem();
        billItem.setAppointment(appointment);
        billItem.setProduct(product);
        billItem.setQuantity(dto.getQuantity());
        billItem.setUnitPriceAtTimeOfSale(product.getPrice());
        billItem.setSubtotal(product.getPrice() * dto.getQuantity());

        BillItem saved = billItemRepository.save(billItem);

        return toResponseDTO(saved);
    }

    public List<BillItemResponseDTO> getBillItemsByAppointmentId(Long appointmentId) {
        return billItemRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Small private helper since BillItem doesn't need a separate
    // Mapper class - it has no complex multi-id resolution like
    // Prescription does (we already have the Appointment/Product
    // objects in hand by the time we build the response).
    private BillItemResponseDTO toResponseDTO(BillItem billItem) {
        return new BillItemResponseDTO(
                billItem.getId(),
                billItem.getAppointment().getId(),
                billItem.getProduct().getId(),
                billItem.getProduct().getName(),
                billItem.getQuantity(),
                billItem.getUnitPriceAtTimeOfSale(),
                billItem.getSubtotal(),
                billItem.getCreatedAt()
        );
    }
}