package com.smartbiz.dto;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private Long doctorId;
    private String doctorName;
    private Long visitTypeId;
    private String visitTypeName;
    private LocalDateTime appointmentDate;
    private String status;
    private LocalDateTime createdAt;

    public AppointmentResponseDTO(Long id, Long userId, String userName, Long doctorId,
                                   String doctorName, Long visitTypeId, String visitTypeName,
                                   LocalDateTime appointmentDate, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.visitTypeId = visitTypeId;
        this.visitTypeName = visitTypeName;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Long getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public Long getVisitTypeId() { return visitTypeId; }
    public String getVisitTypeName() { return visitTypeName; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}