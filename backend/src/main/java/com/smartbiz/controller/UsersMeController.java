package com.smartbiz.controller;

import com.smartbiz.dto.MeResponseDTO;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.StaffMember;
import com.smartbiz.model.User;
import com.smartbiz.repository.StaffMemberRepository;
import com.smartbiz.repository.UserRepository;
import com.smartbiz.security.AuthHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/users/me
 * Returns the currently logged-in user's profile using the JWT token.
 * Checks role first, then fetches from the correct table:
 * - STAFF  → StaffMember table
 * - PATIENT → User table
 * No request body or path variable needed - userId and role come
 * from the JWT via AuthHelper.
 */
@RestController
@RequestMapping("/api/users")
public class UsersMeController {

    private final AuthHelper authHelper;
    private final StaffMemberRepository staffMemberRepository;
    private final UserRepository userRepository;

    public UsersMeController(AuthHelper authHelper,
                              StaffMemberRepository staffMemberRepository,
                              UserRepository userRepository) {
        this.authHelper = authHelper;
        this.staffMemberRepository = staffMemberRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> getMe() {
        Long userId = authHelper.getAuthenticatedUserId();
        String role = authHelper.getAuthenticatedRole();

        MeResponseDTO response;

        if ("ROLE_STAFF".equals(role)) {
            StaffMember staff = staffMemberRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("StaffMember", userId));
            response = new MeResponseDTO(
                    staff.getId(),
                    staff.getFullName(),
                    staff.getUsername(),
                    "STAFF",
                    null // StaffMember has no email field yet
            );
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));
            response = new MeResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getUsername(),
                    "PATIENT",
                    user.getEmail()
            );
        }

        return ResponseEntity.ok(response);
    }
}