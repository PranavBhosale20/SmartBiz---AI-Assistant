package com.smartbiz.dto;

public class AppointmentRequestDTO {

    private Long userId;

    // CHANGED: was "private String doctorName" - now the client sends
    // the doctor's ID instead of typing their name as free text. This
    // matches how userId already works - the client picks from a list
    // of real doctors (with real ids) rather than typing a name that
    // could be misspelled or might not match any real doctor at all.
    private Long doctorId;

    private String appointmentDate;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
}