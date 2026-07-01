package com.smartbiz.repository;

import com.smartbiz.model.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, Long> {

    // Used by the staff login endpoint to find the account matching
    // the submitted username, before verifying the password.
    Optional<StaffMember> findByUsername(String username);
}