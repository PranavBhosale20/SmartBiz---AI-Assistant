package com.smartbiz.dto;

import java.time.LocalTime;

public class DoctorRequestDTO {

    private String name;
    private String specialization;
    private String opdStartTime; // received as "10:00" text, parsed later
    private String opdEndTime;
    private int slotDurationMinutes;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getOpdStartTime() { return opdStartTime; }
    public void setOpdStartTime(String opdStartTime) { this.opdStartTime = opdStartTime; }

    public String getOpdEndTime() { return opdEndTime; }
    public void setOpdEndTime(String opdEndTime) { this.opdEndTime = opdEndTime; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }
}