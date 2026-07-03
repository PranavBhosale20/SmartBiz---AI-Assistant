package com.smartbiz.dto;

public class PrescriptionRequestDTO {

    // CHANGED: was userId + doctorId separately. Now the client just
    // tells us WHICH appointment this prescription belongs to - the
    // patient and doctor are already known from that appointment, no
    // need to send them again.
    private Long appointmentId;

    private Long productId;
    private int quantityPrescribed;
    private String dosageInstructions;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getQuantityPrescribed() { return quantityPrescribed; }
    public void setQuantityPrescribed(int quantityPrescribed) { this.quantityPrescribed = quantityPrescribed; }

    public String getDosageInstructions() { return dosageInstructions; }
    public void setDosageInstructions(String dosageInstructions) { this.dosageInstructions = dosageInstructions; }
}