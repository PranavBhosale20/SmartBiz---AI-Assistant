package com.smartbiz.repository;

import com.smartbiz.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Lets us look up a doctor by their name - useful for the
    // available-slots feature, where the client will ask "what slots
    // does Dr. Mehta have today?" by NAME, not by knowing their id.
    Optional<Doctor> findByName(String name);
}