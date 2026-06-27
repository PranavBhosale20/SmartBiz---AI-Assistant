package com.smartbiz.dto;

// What the client sends when adding or updating a product.
// No "id" here - the database assigns that automatically when
// a new row is created, the client never gets to pick it.
public class ProductRequestDTO {

    private String name;
    private String category;
    private int quantity;
    private double price;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}