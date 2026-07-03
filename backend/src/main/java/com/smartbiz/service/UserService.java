package com.smartbiz.service;

import com.smartbiz.dto.UserMapper;
import com.smartbiz.dto.UserRequestDTO;
import com.smartbiz.dto.UserResponseDTO;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.User;
import com.smartbiz.repository.UserRepository;
import com.smartbiz.security.AuthHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    // NEW (Phase 6): needed for ownership checks on profile access.
    private final AuthHelper authHelper;

    public UserService(UserRepository userRepository, AuthHelper authHelper) {
        this.userRepository = userRepository;
        this.authHelper = authHelper;
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        // STAFF-only in SecurityConfig - no ownership check needed.
        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return UserMapper.toResponseDTO(saved);
    }

    public List<UserResponseDTO> getAllUsers() {
        // STAFF-only in SecurityConfig - no ownership check needed.
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // NEW (Phase 6): PATIENT can only view their own profile.
        // STAFF can view any user's profile.
        if (authHelper.isPatient()) {
            Long callerUserId = authHelper.getAuthenticatedUserId();
            if (!callerUserId.equals(id)) {
                throw new BusinessException(
                        "You are not authorized to view this profile!");
            }
        }

        return UserMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // NEW (Phase 6): PATIENT can only update their own profile.
        if (authHelper.isPatient()) {
            Long callerUserId = authHelper.getAuthenticatedUserId();
            if (!callerUserId.equals(id)) {
                throw new BusinessException(
                        "You are not authorized to update this profile!");
            }
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        User updated = userRepository.save(user);
        return UserMapper.toResponseDTO(updated);
    }

    public void deleteUser(Long id) {
        // STAFF-only in SecurityConfig - no ownership check needed.
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.deleteById(id);
    }
}