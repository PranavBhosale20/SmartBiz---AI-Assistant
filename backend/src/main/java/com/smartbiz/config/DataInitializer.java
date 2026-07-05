package com.smartbiz.config;

import com.smartbiz.model.StaffMember;
import com.smartbiz.repository.StaffMemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs once on every application startup.
 * Checks if any STAFF account exists - if not, creates a default
 * admin account so the system can be bootstrapped on a fresh DB.
 *
 * WHY CommandLineRunner: Spring Boot calls run() automatically after
 * the application context is fully loaded - all beans (including
 * repositories and password encoder) are ready to use. It's the
 * standard Spring Boot pattern for startup initialization logic.
 *
 * Default credentials (change after first login in production):
 * Username: superadmin
 * Password: superadmin123
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final StaffMemberRepository staffMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(StaffMemberRepository staffMemberRepository,
                            PasswordEncoder passwordEncoder) {
        this.staffMemberRepository = staffMemberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Check if any STAFF account already exists
        if (staffMemberRepository.count() > 0) {
            System.out.println("[SmartBiz] Default admin account already exists. Skipping.");
            return;
        }

        // No staff exists - create the default admin account
        StaffMember admin = new StaffMember();
        admin.setFullName("Administrator");
        admin.setUsername("superadmin");
        admin.setPassword(passwordEncoder.encode("superadmin123"));
        admin.setRole("STAFF");

        staffMemberRepository.save(admin);
        System.out.println("[SmartBiz] Default admin account created. Username: superadmin, Password: superadmin123");
        System.out.println("[SmartBiz] Please change the default password after first login.");
    }
}