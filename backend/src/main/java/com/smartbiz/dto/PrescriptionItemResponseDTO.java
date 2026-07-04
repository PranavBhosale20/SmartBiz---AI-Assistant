package com.smartbiz.dto;

public class PrescriptionItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantityPrescribed;
    private String dosageInstructions;

    public PrescriptionItemResponseDTO(Long id, Long productId, String productName,
                                        int quantityPrescribed, String dosageInstructions) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantityPrescribed = quantityPrescribed;
        this.dosageInstructions = dosageInstructions;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantityPrescribed() { return quantityPrescribed; }
    public String getDosageInstructions() { return dosageInstructions; }
}