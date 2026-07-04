package com.smartbiz.dto;

import java.time.LocalDateTime;

public class DashboardSummaryDTO {

    private long totalPatients;
    private long totalDoctors;
    private long appointmentsToday;
    private long lowStockItems;
    private LocalDateTime generatedAt;

    public DashboardSummaryDTO(long totalPatients, long totalDoctors,
                                long appointmentsToday, long lowStockItems) {
        this.totalPatients = totalPatients;
        this.totalDoctors = totalDoctors;
        this.appointmentsToday = appointmentsToday;
        this.lowStockItems = lowStockItems;
        this.generatedAt = LocalDateTime.now();
    }

    public long getTotalPatients() { return totalPatients; }
    public long getTotalDoctors() { return totalDoctors; }
    public long getAppointmentsToday() { return appointmentsToday; }
    public long getLowStockItems() { return lowStockItems; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}