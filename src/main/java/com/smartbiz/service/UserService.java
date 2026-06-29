package com.smartbiz.service;

import com.smartbiz.dto.UserMapper;
import com.smartbiz.dto.UserRequestDTO;
import com.smartbiz.dto.UserResponseDTO;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.User;
import com.smartbiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return UserMapper.toResponseDTO(saved);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        // CHANGED (Phase 5): ResourceNotFoundException instead of
        // RuntimeException - GlobalExceptionHandler now turns this
        // into a clean 404 JSON body instead of a raw stack trace.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return UserMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // We only update the fields - we deliberately don't replace
        // the whole object, so things like "id" and "createdAt" (which
        // the client never sent) stay untouched.
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        User updated = userRepository.save(user);
        return UserMapper.toResponseDTO(updated);
    }

    public void deleteUser(Long id) {
        // Check it exists first - deleting a non-existent id would
        // otherwise fail silently or throw a less helpful database error.
        userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.deleteById(id);
    }
}