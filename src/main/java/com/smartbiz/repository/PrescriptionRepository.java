package com.smartbiz.repository;

import com.smartbiz.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // CHANGED: findByUserId and findByDoctorId removed - Prescription
    // no longer has direct userId/doctorId fields. If we need "all
    // prescriptions for this user" later, we'd write it differently,
    // e.g. findByAppointment_User_Id(Long userId) - reaching THROUGH
    // the appointment relationship - but we don't need that right now,
    // so we're keeping this repository minimal and correct rather than
    // guessing ahead.
    List<Prescription> findByAppointmentId(Long appointmentId);
}