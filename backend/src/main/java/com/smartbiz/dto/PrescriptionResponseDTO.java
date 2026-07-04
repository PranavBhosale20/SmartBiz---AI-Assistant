package com.smartbiz.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionResponseDTO {
    private Long id;
    private Long appointmentId;
    private String patientName;
    private String doctorName;
    private String notes;
    private List<PrescriptionItemResponseDTO> items;
    private LocalDateTime createdAt;

    public PrescriptionResponseDTO(Long id, Long appointmentId, String patientName,
                                    String doctorName, String notes,
                                    List<PrescriptionItemResponseDTO> items,
                                    LocalDateTime createdAt) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.notes = notes;
        this.items = items;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getAppointmentId() { return appointmentId; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getNotes() { return notes; }
    public List<PrescriptionItemResponseDTO> getItems() { return items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}