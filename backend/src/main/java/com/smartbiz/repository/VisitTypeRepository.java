package com.smartbiz.repository;

import com.smartbiz.model.VisitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VisitTypeRepository extends JpaRepository<VisitType, Long> {
    Optional<VisitType> findByName(String name);
}