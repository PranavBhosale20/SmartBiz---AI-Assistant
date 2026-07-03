package com.smartbiz.controller;

import com.smartbiz.dto.UserRequestDTO;
import com.smartbiz.dto.UserResponseDTO;
import com.smartbiz.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + automatic JSON conversion. Without it,
// returning a Java object from a method would just give you the
// object's toString() as plain text. With it, Spring uses Jackson to
// turn our DTOs into proper JSON automatically.
@RestController
// Every method below builds on this base path. So @PostMapping here
// actually means POST /api/users, not just POST /.
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Same pattern as before - constructor injection, Spring supplies
    // the UserService automatically.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @RequestBody tells Spring "take the incoming JSON from the
    // request and convert it into a UserRequestDTO object for me."
    // This is the client -> server direction.
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserRequestDTO dto) {
        UserResponseDTO created = userService.createUser(dto);
        // ResponseEntity.ok(...) sends back HTTP 200 with the DTO as
        // the JSON body. Notice: we NEVER touch the User entity here,
        // only the DTO - the controller has no idea entities even exist.
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // @PathVariable pulls the {id} part out of the URL itself, e.g.
    // GET /api/users/5 gives us id = 5 here.
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
                                                        @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        // 204 No Content - the standard, correct response for a
        // successful DELETE that has nothing meaningful to send back.
        return ResponseEntity.noContent().build();
    }
}