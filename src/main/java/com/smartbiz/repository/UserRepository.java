package com.smartbiz.repository;

import com.smartbiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);

    // NEW (Phase 6): used by the patient login endpoint to find the
    // account matching the submitted username, before verifying the
    // password against it.
    Optional<User> findByUsername(String username);
}