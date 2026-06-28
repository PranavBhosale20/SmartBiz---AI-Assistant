package com.smartbiz.dto;

import com.smartbiz.model.Appointment;
import com.smartbiz.model.Doctor;
import com.smartbiz.model.User;
import com.smartbiz.repository.DoctorRepository;
import com.smartbiz.repository.UserRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AppointmentMapper {

    private final UserRepository userRepository;

    // NEW: same reason we needed UserRepository - this mapper now
    // also has to resolve a doctorId (just a number) into the real
    // Doctor object before it can be saved on the Appointment entity.
    private final DoctorRepository doctorRepository;

    // Constructor injection - Spring sees this constructor takes TWO
    // repositories now, and supplies both automatically when it
    // builds this mapper bean.
    public AppointmentMapper(UserRepository userRepository, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    public Appointment toEntity(AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + dto.getUserId()));

        // Same lookup pattern as User above, just for Doctor now.
        // If the client sends a doctorId that doesn't exist, we fail
        // loudly here rather than saving a broken appointment.
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException(
                        "Doctor not found with id: " + dto.getDoctorId()));

        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(LocalDateTime.parse(dto.getAppointmentDate()));

        return appointment;
    }

    public AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getUser().getId(),
                appointment.getUser().getName(),
                // Same flattening trick as user - pull the real
                // doctor's id and name out HERE, while the entity is
                // still fully loaded, before it ever reaches the
                // Controller/JSON serialization step.
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getCreatedAt()
        );
    }
}