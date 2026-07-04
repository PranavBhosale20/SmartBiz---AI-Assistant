package com.smartbiz.service;

import com.smartbiz.dto.BillResponseDTO;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.Appointment;
import com.smartbiz.model.Bill;
import com.smartbiz.model.BillItem;
import com.smartbiz.repository.AppointmentRepository;
import com.smartbiz.repository.BillItemRepository;
import com.smartbiz.repository.BillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {

    private static final double FIRST_TIME_FEE = 500.0;

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillItemRepository billItemRepository;

    public BillService(BillRepository billRepository,
                        AppointmentRepository appointmentRepository,
                        BillItemRepository billItemRepository) {
        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
        this.billItemRepository = billItemRepository;
    }

    @Transactional
    public BillResponseDTO generateBill(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));

        Optional<Bill> existing = billRepository.findByAppointmentId(appointmentId);
        if (existing.isPresent()) {
            return toResponseDTO(existing.get());
        }

        List<Appointment> allUserAppointments =
                appointmentRepository.findByUserId(appointment.getUser().getId());

        // Safe .get() - appointment itself guarantees list is non-empty
        Appointment earliestAppointment = allUserAppointments.stream()
                .min(Comparator.comparing(Appointment::getCreatedAt))
                .get();

        double visitFee;
        if (earliestAppointment.getId().equals(appointment.getId())) {
            visitFee = FIRST_TIME_FEE;
        } else {
            visitFee = appointment.getVisitType().getRepeatPrice();
        }

        List<BillItem> billItems = billItemRepository.findByAppointmentId(appointmentId);
        double medicineCost = billItems.stream()
                .mapToDouble(BillItem::getSubtotal)
                .sum();

        Bill bill = new Bill();
        bill.setAppointment(appointment);
        bill.setVisitFee(visitFee);
        bill.setMedicineCost(medicineCost);
        bill.setGrandTotal(visitFee + medicineCost);

        Bill saved = billRepository.save(bill);
        return toResponseDTO(saved);
    }

    public BillResponseDTO getBillByAppointmentId(Long appointmentId) {
        Bill bill = billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No bill found for appointment id: " + appointmentId));
        return toResponseDTO(bill);
    }

    /**
     * CHANGED (Phase 10 prep): converts Bill entity to BillResponseDTO.
     * Extracts nested appointment fields (userId, userName, doctorId,
     * doctorName) so frontend doesn't need to parse a nested entity.
     */
    private BillResponseDTO toResponseDTO(Bill bill) {
        Appointment apt = bill.getAppointment();
        return new BillResponseDTO(
                bill.getId(),
                apt.getId(),
                apt.getUser().getId(),
                apt.getUser().getName(),
                apt.getDoctor().getId(),
                apt.getDoctor().getName(),
                bill.getVisitFee(),
                bill.getMedicineCost(),
                bill.getGrandTotal(),
                bill.getStatus(),
                bill.getCreatedAt()
        );
    }
    
    public BillResponseDTO markAsPaid(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", billId));

        if ("PAID".equals(bill.getStatus())) {
            throw new BusinessException("Bill is already marked as paid!");
        }

        bill.setStatus("PAID");
        Bill updated = billRepository.save(bill);
        return toResponseDTO(updated);
    }
}