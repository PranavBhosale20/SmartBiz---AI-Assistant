package com.smartbiz.dto;

import java.time.LocalDateTime;

public class BillResponseDTO {

    private Long id;
    private Long appointmentId;
    private Long userId;
    private String userName;
    private Long doctorId;
    private String doctorName;
    private double visitFee;
    private double medicineCost;
    private double grandTotal;
    private String status;
    private LocalDateTime createdAt;

    public BillResponseDTO(Long id, Long appointmentId, Long userId,
                            String userName, Long doctorId, String doctorName,
                            double visitFee, double medicineCost, double grandTotal,
                            String status, LocalDateTime createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.userName = userName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.visitFee = visitFee;
        this.medicineCost = medicineCost;
        this.grandTotal = grandTotal;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getAppointmentId() { return appointmentId; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Long getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public double getVisitFee() { return visitFee; }
    public double getMedicineCost() { return medicineCost; }
    public double getGrandTotal() { return grandTotal; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}