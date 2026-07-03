package com.smartbiz.model;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String specialization;

    // LocalTime = just a time-of-day, like 10:00 AM, with NO date
    // attached. Perfect here because "OPD starts at 10am" is true
    // every single day, it's not tied to one specific calendar date
    // the way appointmentDate (a LocalDateTime) is.
    @Column(nullable = false)
    private LocalTime opdStartTime;

    @Column(nullable = false)
    private LocalTime opdEndTime;

    // Minutes per patient slot. Stored per-doctor (not hardcoded as
    // "10" somewhere in our Java logic) so different doctors could
    // have different slot lengths later without touching any code.
    @Column(nullable = false)
    private int slotDurationMinutes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalTime getOpdStartTime() { return opdStartTime; }
    public void setOpdStartTime(LocalTime opdStartTime) { this.opdStartTime = opdStartTime; }

    public LocalTime getOpdEndTime() { return opdEndTime; }
    public void setOpdEndTime(LocalTime opdEndTime) { this.opdEndTime = opdEndTime; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}