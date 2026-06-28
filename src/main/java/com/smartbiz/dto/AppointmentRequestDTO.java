package com.smartbiz.dto;

public class AppointmentRequestDTO {

    private Long userId;
    private Long doctorId;
    private Long visitTypeId;
    private String appointmentDate;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getVisitTypeId() { return visitTypeId; }
    public void setVisitTypeId(Long visitTypeId) { this.visitTypeId = visitTypeId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
}