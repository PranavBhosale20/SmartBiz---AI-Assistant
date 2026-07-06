package com.smartbiz.dto;

import com.smartbiz.model.User;

public class UserMapper {

    /* ==========================================================
       DTO → ENTITY
    ========================================================== */

    public static User toEntity(UserRequestDTO dto) {

        User user = new User();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        user.setGender(dto.getGender());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());

        user.setBloodGroup(dto.getBloodGroup());
        user.setEmergencyContact(dto.getEmergencyContact());

        return user;
    }

    /* ==========================================================
       ENTITY → DTO
    ========================================================== */

    public static UserResponseDTO toResponseDTO(User user) {

        return new UserResponseDTO(

                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),

                user.getGender(),
                user.getDateOfBirth(),
                user.getAddress(),

                user.getBloodGroup(),
                user.getEmergencyContact(),

                user.getProfileImage(),

                user.getCreatedAt()
        );
    }
}