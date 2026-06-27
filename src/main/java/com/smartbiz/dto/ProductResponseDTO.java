package com.smartbiz.dto;

import java.time.LocalDateTime;

// What we send BACK to the client. Has everything the request DTO
// has, plus the database-generated "id" and "createdAt" - things
// the client never sent us, but needs to see in the response.
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private LocalDateTime createdAt;

    public ProductResponseDTO(Long id, String name, String category, int quantity,
                               double price, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}