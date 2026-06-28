package com.smartbiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_types")
public class VisitType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "Checkup", "Consultant". Stored as DATA, not as Java code,
    // so adding a new type later (like "Emergency") is just inserting
    // a new row through the API - no code changes, no redeployment.
    @Column(nullable = false, unique = true)
    private String name;

    // The price charged from a patient's SECOND visit of this type
    // onward. The very first visit a patient EVER has (regardless of
    // type) uses a separate flat ₹500 rate, handled in the billing
    // logic itself rather than stored here.
    @Column(nullable = false)
    private double repeatPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getRepeatPrice() { return repeatPrice; }
    public void setRepeatPrice(double repeatPrice) { this.repeatPrice = repeatPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}