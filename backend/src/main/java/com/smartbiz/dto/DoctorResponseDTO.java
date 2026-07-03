package com.smartbiz.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DoctorResponseDTO {

    private Long id;
    private String name;
    private String specialization;
    private LocalTime opdStartTime;
    private LocalTime opdEndTime;
    private int slotDurationMinutes;
    private LocalDateTime createdAt;

    public DoctorResponseDTO(Long id, String name, String specialization, LocalTime opdStartTime,
                              LocalTime opdEndTime, int slotDurationMinutes, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.opdStartTime = opdStartTime;
        this.opdEndTime = opdEndTime;
        this.slotDurationMinutes = slotDurationMinutes;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public LocalTime getOpdStartTime() { return opdStartTime; }
    public LocalTime getOpdEndTime() { return opdEndTime; }
    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}