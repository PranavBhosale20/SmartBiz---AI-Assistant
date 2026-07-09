package com.smartbiz.dto;

import java.time.LocalDateTime;

public class VisitTypeResponseDTO {

    private Long id;
    private String name;
    private double firstVisitPrice;
    private double repeatPrice;
    private LocalDateTime createdAt;

    public VisitTypeResponseDTO(
            Long id,
            String name,
            double firstVisitPrice,
            double repeatPrice,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.firstVisitPrice = firstVisitPrice;
        this.repeatPrice = repeatPrice;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getFirstVisitPrice() {
        return firstVisitPrice;
    }

    public double getRepeatPrice() {
        return repeatPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}