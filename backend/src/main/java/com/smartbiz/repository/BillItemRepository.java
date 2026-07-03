package com.smartbiz.repository;

import com.smartbiz.model.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {

    // This is the method our future Bill calculation will rely on
    // most - "give me everything charged during this appointment."
    List<BillItem> findByAppointmentId(Long appointmentId);
}