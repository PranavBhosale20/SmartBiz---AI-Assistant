package com.smartbiz.service;

import com.smartbiz.dto.UserMapper;
import com.smartbiz.dto.UserRequestDTO;
import com.smartbiz.dto.UserResponseDTO;
import com.smartbiz.model.User;
import com.smartbiz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

// @Service tells Spring "manage this class as a bean, and its job is
// business logic" - it's really just @Component under the hood with a
// more meaningful name. Spring will create exactly one instance of this
// class and hand it to anything that asks for a UserService.
@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection, as decided earlier. Spring sees this is
    // the only constructor and automatically supplies a UserRepository
    // when building this service - we never write "new UserRepository()".
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Takes what the CLIENT sent (request DTO), converts it to an
    // entity, saves it, then converts the SAVED entity (now with a
    // real database-generated id and createdAt) back into a response DTO.
    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = UserMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return UserMapper.toResponseDTO(saved);
    }

    // Fetches every user, and converts EACH entity into a response DTO.
    // .stream().map(...).collect(...) is just Java's way of saying
    // "do this conversion for every item in the list."
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Looks up one user by id. If it doesn't exist, we throw an error
    // immediately rather than returning null and letting some other
    // part of the code accidentally crash on a null value later.
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

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
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.deleteById(id);
    }
}