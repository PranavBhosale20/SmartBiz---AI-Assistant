package com.smartbiz.dto;

import java.time.LocalDateTime;

public class PrescriptionResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long doctorId;
    private String doctorName;
    private Long productId;
    private String productName;
    private int quantityPrescribed;
    private String dosageInstructions;
    private LocalDateTime createdAt;

    public PrescriptionResponseDTO(Long id, Long userId, String userName, Long doctorId,
                                    String doctorName, Long productId, String productName,
                                    int quantityPrescribed, String dosageInstructions,
                                    LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.productId = productId;
        this.productName = productName;
        this.quantityPrescribed = quantityPrescribed;
        this.dosageInstructions = dosageInstructions;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Long getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantityPrescribed() { return quantityPrescribed; }
    public String getDosageInstructions() { return dosageInstructions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}