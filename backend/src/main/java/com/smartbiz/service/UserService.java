package com.smartbiz.service;

import com.smartbiz.dto.UserMapper;
import com.smartbiz.dto.UserRequestDTO;
import com.smartbiz.dto.UserResponseDTO;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.User;
import com.smartbiz.repository.UserRepository;
import com.smartbiz.security.AuthHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
@Service
public class UserService {

    /* ==========================================================
       DEPENDENCIES
    ========================================================== */

    private final UserRepository userRepository;

    private final AuthHelper authHelper;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       AuthHelper authHelper,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.authHelper = authHelper;
        this.passwordEncoder = passwordEncoder;
    }

    /* ==========================================================
       CREATE USER
    ========================================================== */

    public UserResponseDTO createUser(UserRequestDTO dto) {

        validateUser(dto);

        User user = UserMapper.toEntity(dto);

        /* ==========================================================
           AUTO-GENERATE LOGIN CREDENTIALS
        ========================================================== */

        String baseUsername = dto.getName()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "");

        String username = baseUsername;
        int counter = 1;

        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter++;
        }

        user.setUsername(username);

        user.setPassword(
                passwordEncoder.encode("Patient@123")
        );

        User saved = userRepository.save(user);

        return UserMapper.toResponseDTO(saved);
    }

    /* ==========================================================
       GET ALL USERS
    ========================================================== */

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /* ==========================================================
       GET USER BY ID
    ========================================================== */

    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (authHelper.isPatient()) {

            Long callerUserId = authHelper.getAuthenticatedUserId();

            if (!callerUserId.equals(id)) {
                throw new BusinessException(
                        "You are not authorized to view this profile.");
            }
        }

        return UserMapper.toResponseDTO(user);
    }

    /* ==========================================================
       UPDATE USER
    ========================================================== */

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {

        validateUser(dto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (authHelper.isPatient()) {

            Long callerUserId = authHelper.getAuthenticatedUserId();

            if (!callerUserId.equals(id)) {
                throw new BusinessException(
                        "You are not authorized to update this profile.");
            }
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setBloodGroup(dto.getBloodGroup());
        user.setEmergencyContact(dto.getEmergencyContact());

        User updated = userRepository.save(user);

        return UserMapper.toResponseDTO(updated);
    }

    /* ==========================================================
       DELETE USER
    ========================================================== */

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", id));

        try {

            userRepository.delete(user);

        } catch (DataIntegrityViolationException ex) {

            throw new BusinessException(
                    "Cannot delete patient because appointments are linked to this patient.");
        }
    }

    /* ==========================================================
       VALIDATION
    ========================================================== */

    private void validateUser(UserRequestDTO dto) {

        if (dto.getDateOfBirth() != null &&
                dto.getDateOfBirth().isAfter(LocalDate.now())) {

            throw new BusinessException(
                    "Date of birth cannot be in the future.");
        }
    }
}