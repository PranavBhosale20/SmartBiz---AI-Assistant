package com.smartbiz.dto;

import java.time.LocalDateTime;

public class PrescriptionResponseDTO {

    private Long id;
    private Long appointmentId;

    // We still flatten OUT the patient/doctor names for display
    // convenience, even though they're not stored directly on
    // Prescription anymore - the Mapper will pull them from the
    // linked Appointment when building this response.
    private String userName;
    private String doctorName;

    private Long productId;
    private String productName;
    private int quantityPrescribed;
    private String dosageInstructions;
    private LocalDateTime createdAt;

    public PrescriptionResponseDTO(Long id, Long appointmentId, String userName, String doctorName,
                                    Long productId, String productName, int quantityPrescribed,
                                    String dosageInstructions, LocalDateTime createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.userName = userName;
        this.doctorName = doctorName;
        this.productId = productId;
        this.productName = productName;
        this.quantityPrescribed = quantityPrescribed;
        this.dosageInstructions = dosageInstructions;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getAppointmentId() { return appointmentId; }
    public String getUserName() { return userName; }
    public String getDoctorName() { return doctorName; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantityPrescribed() { return quantityPrescribed; }
    public String getDosageInstructions() { return dosageInstructions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}