package com.smartbiz.dto;

import com.smartbiz.model.Doctor;
import java.time.LocalTime;

// No relationships to resolve here, so this stays a simple static
// mapper - same reasoning as ProductMapper.
public class DoctorMapper {

    public static Doctor toEntity(DoctorRequestDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        // The client sends times as plain text like "10:00" - LocalTime.parse()
        // converts that text into a real LocalTime object.
        doctor.setOpdStartTime(LocalTime.parse(dto.getOpdStartTime()));
        doctor.setOpdEndTime(LocalTime.parse(dto.getOpdEndTime()));
        doctor.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        return doctor;
    }

    public static DoctorResponseDTO toResponseDTO(Doctor doctor) {
        return new DoctorResponseDTO(
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getOpdStartTime(),
                doctor.getOpdEndTime(),
                doctor.getSlotDurationMinutes(),
                doctor.getCreatedAt()
        );
    }
}