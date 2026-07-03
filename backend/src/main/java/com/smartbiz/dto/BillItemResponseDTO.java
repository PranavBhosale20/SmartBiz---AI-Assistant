package com.smartbiz.dto;

import java.time.LocalDateTime;

public class BillItemResponseDTO {

    private Long id;
    private Long appointmentId;
    private Long productId;
    private String productName;
    private int quantity;
    private double unitPriceAtTimeOfSale;
    private double subtotal;
    private LocalDateTime createdAt;

    public BillItemResponseDTO(Long id, Long appointmentId, Long productId, String productName,
                                int quantity, double unitPriceAtTimeOfSale, double subtotal,
                                LocalDateTime createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPriceAtTimeOfSale = unitPriceAtTimeOfSale;
        this.subtotal = subtotal;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getAppointmentId() { return appointmentId; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPriceAtTimeOfSale() { return unitPriceAtTimeOfSale; }
    public double getSubtotal() { return subtotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}