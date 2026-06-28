package com.smartbiz.repository;

import com.smartbiz.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // Lets us pull up a patient's full prescription history - useful
    // for a doctor to see what a patient has been given before.
    List<Prescription> findByUserId(Long userId);

    // Lets us see everything a specific doctor has prescribed.
    List<Prescription> findByDoctorId(Long doctorId);
}