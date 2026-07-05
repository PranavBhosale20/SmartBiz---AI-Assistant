package com.smartbiz.service;

import com.smartbiz.dto.*;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.model.StaffMember;
import com.smartbiz.model.User;
import com.smartbiz.repository.StaffMemberRepository;
import com.smartbiz.repository.UserRepository;
import com.smartbiz.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final StaffMemberRepository staffMemberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(StaffMemberRepository staffMemberRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.staffMemberRepository = staffMemberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO registerStaff(StaffRegisterDTO dto) {
        if (staffMemberRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new BusinessException("Username already taken: " + dto.getUsername());
        }

        StaffMember staff = new StaffMember();
        staff.setFullName(dto.getFullName());
        staff.setUsername(dto.getUsername());
        staff.setPassword(passwordEncoder.encode(dto.getPassword()));
        staff.setRole(dto.getRole());

        StaffMember saved = staffMemberRepository.save(staff);
        String token = jwtUtil.generateToken(saved.getUsername(), saved.getRole(), saved.getId());
        return new AuthResponseDTO(token, saved.getUsername(), saved.getRole());
    }

    public AuthResponseDTO registerPatient(PatientRegisterDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new BusinessException("Username already taken: " + dto.getUsername());
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // NEW: save gender from registration DTO
        user.setGender(dto.getGender());

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getUsername(), "PATIENT", saved.getId());
        return new AuthResponseDTO(token, saved.getUsername(), saved.getName(), "PATIENT", saved.getGender());
    }

    public AuthResponseDTO loginStaff(LoginRequestDTO dto) {
        StaffMember staff = staffMemberRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), staff.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(staff.getUsername(), staff.getRole(), staff.getId());
        // STAFF login - no fullName or gender in response
        return new AuthResponseDTO(token, staff.getUsername(), staff.getRole());
    }

    public AuthResponseDTO loginPatient(LoginRequestDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), "PATIENT", user.getId());
        // PATIENT login - include fullName and gender
        return new AuthResponseDTO(token, user.getUsername(), user.getName(), "PATIENT", user.getGender());
    }
}