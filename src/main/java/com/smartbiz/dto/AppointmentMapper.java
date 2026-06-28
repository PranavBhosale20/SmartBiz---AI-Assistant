package com.smartbiz.dto;

import com.smartbiz.model.Appointment;
import com.smartbiz.model.Doctor;
import com.smartbiz.model.User;
import com.smartbiz.model.VisitType;
import com.smartbiz.repository.DoctorRepository;
import com.smartbiz.repository.UserRepository;
import com.smartbiz.repository.VisitTypeRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AppointmentMapper {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final VisitTypeRepository visitTypeRepository;

    public AppointmentMapper(UserRepository userRepository, DoctorRepository doctorRepository,
                              VisitTypeRepository visitTypeRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.visitTypeRepository = visitTypeRepository;
    }

    public Appointment toEntity(AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + dto.getDoctorId()));

        VisitType visitType = visitTypeRepository.findById(dto.getVisitTypeId())
                .orElseThrow(() -> new RuntimeException("VisitType not found with id: " + dto.getVisitTypeId()));

        appointment.setUser(user);
        appointment.setDoctor(doctor);
        appointment.setVisitType(visitType);
        appointment.setAppointmentDate(LocalDateTime.parse(dto.getAppointmentDate()));

        return appointment;
    }

    public AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getUser().getId(),
                appointment.getUser().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getVisitType().getId(),
                appointment.getVisitType().getName(),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getCreatedAt()
        );
    }
}