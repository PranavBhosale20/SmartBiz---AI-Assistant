package com.smartbiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_types")
public class VisitType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "first_visit_price", nullable = false)
    private double firstVisitPrice;

    @Column(name = "repeat_price", nullable = false)
    private double repeatPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFirstVisitPrice() {
        return firstVisitPrice;
    }

    public void setFirstVisitPrice(double firstVisitPrice) {
        this.firstVisitPrice = firstVisitPrice;
    }

    public double getRepeatPrice() {
        return repeatPrice;
    }

    public void setRepeatPrice(double repeatPrice) {
        this.repeatPrice = repeatPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}