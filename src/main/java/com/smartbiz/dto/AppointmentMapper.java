package com.smartbiz.dto;

import com.smartbiz.model.Appointment;
import com.smartbiz.model.Doctor;
import com.smartbiz.model.User;
import com.smartbiz.model.VisitType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * CHANGED (Phase 5): toEntity() used to take just the
 * AppointmentRequestDTO and look up the User, Doctor, and VisitType
 * itself via repositories it held (UserRepository, DoctorRepository,
 * VisitTypeRepository), throwing RuntimeException if any id didn't
 * exist. That made this "mapper" secretly do repository work, which
 * isn't really what a mapper should do - it should just reshape data
 * it's already been GIVEN, not go fetch data on its own.
 *
 * Now toEntity() takes the already-resolved User, Doctor, and
 * VisitType directly. AppointmentService resolves them first (and
 * throws ResourceNotFoundException if any are missing) before calling
 * this method. Because of that, this class no longer needs
 * UserRepository/DoctorRepository/VisitTypeRepository injected at
 * all - it has zero database dependency now, and zero exceptions of
 * its own. It's a pure mapper.
 */
@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDTO dto, User user, Doctor doctor, VisitType visitType) {
        Appointment appointment = new Appointment();
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