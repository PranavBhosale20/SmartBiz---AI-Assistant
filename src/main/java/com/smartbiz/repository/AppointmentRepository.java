package com.smartbiz.repository;

import com.smartbiz.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // "findByUserId" - Spring reads this and knows to look inside the
    // Appointment entity for a field called "user" (the @ManyToOne
    // relationship), then filter by that user's id. It generates:
    // SELECT * FROM appointments WHERE user_id = ?
    List<Appointment> findByUserId(Long userId);

    // Useful later for filtering appointments by their booking status
    // (e.g. show only "BOOKED" ones, hide "CANCELLED" ones).
    List<Appointment> findByStatus(String status);

    List<Appointment> findByDoctorName(String doctorName);
}