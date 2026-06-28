package com.smartbiz.dto;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {

    private Long id;
    private Long userId;
    private String userName;

    // CHANGED: we now send back BOTH the doctor's id and their name,
    // same flattening idea as userId/userName above. The client gets
    // a readable name to display, but also the id in case it needs to
    // reference that doctor again later (e.g. for the slots endpoint).
    private Long doctorId;
    private String doctorName;

    private LocalDateTime appointmentDate;
    private String status;
    private LocalDateTime createdAt;

    public AppointmentResponseDTO(Long id, Long userId, String userName, Long doctorId,
                                   String doctorName, LocalDateTime appointmentDate,
                                   String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public Long getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}