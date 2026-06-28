package com.smartbiz.service;

import com.smartbiz.model.Appointment;
import com.smartbiz.model.Bill;
import com.smartbiz.model.BillItem;
import com.smartbiz.repository.AppointmentRepository;
import com.smartbiz.repository.BillItemRepository;
import com.smartbiz.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private static final double FIRST_TIME_FEE = 500.0;

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillItemRepository billItemRepository;

    public BillService(BillRepository billRepository, AppointmentRepository appointmentRepository,
                        BillItemRepository billItemRepository) {
        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
        this.billItemRepository = billItemRepository;
    }

    @Transactional
    public Bill generateBill(Long appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        // A bill should only ever be generated once per appointment -
        // if one already exists, just return it instead of creating
        // a duplicate.
        Optional<Bill> existing = billRepository.findByAppointmentId(appointmentId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // --- Step 1: figure out the visit fee ---
        // Fetch EVERY appointment this user has ever had, then find
        // the one with the EARLIEST createdAt (when it was booked,
        // not the appointment's scheduled date/time). If THIS
        // appointment's id matches that earliest one, it's their
        // first visit ever.
        List<Appointment> allUserAppointments = appointmentRepository.findByUserId(appointment.getUser().getId());

        Appointment earliestAppointment = allUserAppointments.stream()
                .min(Comparator.comparing(Appointment::getCreatedAt))
                .orElseThrow(() -> new RuntimeException("No appointments found for this user - unexpected state"));

        double visitFee;
        if (earliestAppointment.getId().equals(appointment.getId())) {
            // This IS their first-ever appointment.
            visitFee = FIRST_TIME_FEE;
        } else {
            // Not their first visit - use whatever this specific
            // appointment's visit type charges for repeat visits.
            visitFee = appointment.getVisitType().getRepeatPrice();
        }

        // --- Step 2: sum up medicine/product costs for this visit ---
        List<BillItem> billItems = billItemRepository.findByAppointmentId(appointmentId);
        double medicineCost = billItems.stream()
                .mapToDouble(BillItem::getSubtotal)
                .sum();

        // --- Step 3: build and save the bill ---
        Bill bill = new Bill();
        bill.setAppointment(appointment);
        bill.setVisitFee(visitFee);
        bill.setMedicineCost(medicineCost);
        bill.setGrandTotal(visitFee + medicineCost);

        return billRepository.save(bill);
    }

    public Bill getBillByAppointmentId(Long appointmentId) {
        return billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("No bill found for appointment id: " + appointmentId));
    }
}