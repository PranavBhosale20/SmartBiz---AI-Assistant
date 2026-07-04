package com.smartbiz.controller;

import com.smartbiz.dto.DashboardSummaryDTO;
import com.smartbiz.repository.AppointmentRepository;
import com.smartbiz.repository.DoctorRepository;
import com.smartbiz.repository.ProductRepository;
import com.smartbiz.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Dashboard endpoints - aggregates data from multiple repositories
 * into single responses so frontend doesn't need multiple API calls.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    // Low stock threshold - products with quantity at or below
    // this number are flagged as low stock on the dashboard.
    private static final int LOW_STOCK_THRESHOLD = 10;

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProductRepository productRepository;

    public DashboardController(UserRepository userRepository,
                                DoctorRepository doctorRepository,
                                AppointmentRepository appointmentRepository,
                                ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.productRepository = productRepository;
    }

    /**
     * GET /api/dashboard/summary
     * Returns key clinic stats in one call.
     * appointmentsToday: counts non-cancelled appointments for today.
     * lowStock: products with quantity <= LOW_STOCK_THRESHOLD (10).
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        long patients = userRepository.count();
        long doctors = doctorRepository.count();

        // Today's appointments - fetch between start and end of today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        long appointmentsToday = appointmentRepository
                .findByAppointmentDateBetween(startOfDay, endOfDay)
                .stream()
                .filter(a -> !a.getStatus().equals("CANCELLED"))
                .count();

        // Low stock count
        long lowStock = productRepository.findAll()
                .stream()
                .filter(p -> p.getQuantity() <= LOW_STOCK_THRESHOLD)
                .count();

        return ResponseEntity.ok(
                new DashboardSummaryDTO(patients, doctors, appointmentsToday, lowStock));
    }

    /**
     * GET /api/dashboard/charts
     * Weekly appointments (real data) + monthly revenue (placeholder zeros
     * until a proper revenue aggregation query is built).
     */
    @GetMapping("/charts")
    public ResponseEntity<Map<String, Object>> getCharts() {
        // Weekly appointments - last 7 days, one count per day
        List<Map<String, Object>> weeklyAppointments = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            long count = appointmentRepository
                    .findByAppointmentDateBetween(start, end)
                    .stream()
                    .filter(a -> !a.getStatus().equals("CANCELLED"))
                    .count();
            weeklyAppointments.add(Map.of(
                    "date", date.toString(),
                    "count", count
            ));
        }

        // Monthly revenue - placeholder zeros for now
        // TODO: implement real revenue aggregation in a later phase
        List<Map<String, Object>> monthlyRevenue = new java.util.ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            monthlyRevenue.add(Map.of("month", month, "revenue", 0));
        }

        return ResponseEntity.ok(Map.of(
                "weeklyAppointments", weeklyAppointments,
                "monthlyRevenue", monthlyRevenue
        ));
    }
}