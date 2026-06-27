package com.smartbiz.dto;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {

    private Long id;
    private Long userId;
    private String userName;
    private String doctorName;
    private LocalDateTime appointmentDate;
    private String status;
    private LocalDateTime createdAt;

    public AppointmentResponseDTO(Long id, Long userId, String userName, String doctorName,
                                   LocalDateTime appointmentDate, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getDoctorName() { return doctorName; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}