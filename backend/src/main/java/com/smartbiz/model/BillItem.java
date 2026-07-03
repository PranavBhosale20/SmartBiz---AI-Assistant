package com.smartbiz.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bill_items")
public class BillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Every bill item belongs to a specific visit - this is how we'll
    // later total up "everything charged during this appointment."
    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    // IMPORTANT: we store the price PER UNIT at the exact moment this
    // bill item was created, rather than looking up product.getPrice()
    // live every time we calculate a bill. Why? Because product
    // prices can change over time (medicine costs go up) - if we
    // looked it up live, an OLD bill from 3 months ago would suddenly
    // show today's price, which is factually wrong. Capturing the
    // price at time-of-sale is what real billing/invoicing systems do.
    @Column(nullable = false)
    private double unitPriceAtTimeOfSale;

    @Column(nullable = false)
    private double subtotal;

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

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPriceAtTimeOfSale() { return unitPriceAtTimeOfSale; }
    public void setUnitPriceAtTimeOfSale(double unitPriceAtTimeOfSale) { this.unitPriceAtTimeOfSale = unitPriceAtTimeOfSale; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}