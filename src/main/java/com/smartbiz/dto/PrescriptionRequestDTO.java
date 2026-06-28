package com.smartbiz.dto;

public class PrescriptionRequestDTO {

    private Long userId;
    private Long doctorId;
    private Long productId;
    private int quantityPrescribed;
    private String dosageInstructions;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getQuantityPrescribed() { return quantityPrescribed; }
    public void setQuantityPrescribed(int quantityPrescribed) { this.quantityPrescribed = quantityPrescribed; }

    public String getDosageInstructions() { return dosageInstructions; }
    public void setDosageInstructions(String dosageInstructions) { this.dosageInstructions = dosageInstructions; }
}