package com.smartbiz.dto;

public class VisitTypeRequestDTO {

    private String name;
    private double firstVisitPrice;
    private double repeatPrice;

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
}