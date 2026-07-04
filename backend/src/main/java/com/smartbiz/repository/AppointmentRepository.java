package com.smartbiz.repository;

import com.smartbiz.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUserId(Long userId);

    List<Appointment> findByStatus(String status);

    List<Appointment> findByDoctorId(Long doctorId);

    // Finds a doctor's appointments that fall between two timestamps.
    // We pass in "start of day" and "end of day" for whatever date
    // we're checking, so this effectively means "this doctor's
    // appointments on this specific date." "Between" is one of Spring
    // Data JPA's built-in keywords - it generates:
    // WHERE doctor_id = ? AND appointment_date BETWEEN ? AND ?
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(
            Long doctorId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
 // Used by DashboardController to count today's appointments
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
}