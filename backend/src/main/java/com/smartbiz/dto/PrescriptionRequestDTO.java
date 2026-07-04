package com.smartbiz.dto;

import java.util.List;

public class PrescriptionRequestDTO {
    private Long appointmentId;
    private String notes;
    private List<PrescriptionItemDTO> items;

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<PrescriptionItemDTO> getItems() { return items; }
    public void setItems(List<PrescriptionItemDTO> items) { this.items = items; }
}