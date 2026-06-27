package com.smartbiz.dto;

public class AppointmentRequestDTO {

    private Long userId;
    private String doctorName;
    private String appointmentDate; // received as ISO string from client, parsed later

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
}