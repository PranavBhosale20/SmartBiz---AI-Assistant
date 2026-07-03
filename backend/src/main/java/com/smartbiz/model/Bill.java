package com.smartbiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One bill per appointment - this is a one-to-one relationship,
    // a new kind we haven't used yet. @JoinColumn still works the
    // same way, but unique = true enforces that no two Bills can
    // ever point to the same Appointment.
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false)
    private double visitFee;

    @Column(nullable = false)
    private double medicineCost;

    @Column(nullable = false)
    private double grandTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public double getVisitFee() { return visitFee; }
    public void setVisitFee(double visitFee) { this.visitFee = visitFee; }

    public double getMedicineCost() { return medicineCost; }
    public void setMedicineCost(double medicineCost) { this.medicineCost = medicineCost; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}