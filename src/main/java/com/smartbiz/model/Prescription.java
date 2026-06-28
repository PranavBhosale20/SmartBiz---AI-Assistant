package com.smartbiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The patient receiving this prescription.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The doctor who prescribed it - captured directly here, NOT
    // through an Appointment, since we deliberately decided a doctor
    // might prescribe something outside a formally booked visit
    // (follow-up, walk-in, etc).
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Which medicine from our inventory was prescribed.
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantityPrescribed;

    // Free text like "2 tablets twice a day for 5 days" - we're not
    // trying to structure this into separate fields (dose amount,
    // frequency, duration) since that adds complexity without much
    // real benefit for this project's scope. A doctor just writes
    // what they'd normally write on a prescription pad.
    @Column(length = 500)
    private String dosageInstructions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantityPrescribed() { return quantityPrescribed; }
    public void setQuantityPrescribed(int quantityPrescribed) { this.quantityPrescribed = quantityPrescribed; }

    public String getDosageInstructions() { return dosageInstructions; }
    public void setDosageInstructions(String dosageInstructions) { this.dosageInstructions = dosageInstructions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}