package com.smartbiz.service;

import com.smartbiz.dto.AppointmentMapper;
import com.smartbiz.dto.AppointmentRequestDTO;
import com.smartbiz.dto.AppointmentResponseDTO;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.Appointment;
import com.smartbiz.model.Doctor;
import com.smartbiz.model.User;
import com.smartbiz.model.VisitType;
import com.smartbiz.repository.AppointmentRepository;
import com.smartbiz.repository.DoctorRepository;
import com.smartbiz.repository.UserRepository;
import com.smartbiz.repository.VisitTypeRepository;
import com.smartbiz.security.AuthHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final VisitTypeRepository visitTypeRepository;

    // NEW (Phase 6): reads who is making the current request from
    // the JWT token (already parsed by JwtAuthFilter).
    private final AuthHelper authHelper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               AppointmentMapper appointmentMapper,
                               DoctorRepository doctorRepository,
                               UserRepository userRepository,
                               VisitTypeRepository visitTypeRepository,
                               AuthHelper authHelper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.visitTypeRepository = visitTypeRepository;
        this.authHelper = authHelper;
    }

    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO dto) {
        // NEW (Phase 6): ownership check on booking.
        // A PATIENT can only book for themselves - they can't supply
        // someone else's userId in the request body and book on their
        // behalf. STAFF can book for any patient (receptionist booking
        // a walk-in, for example).
        if (authHelper.isPatient()) {
            Long callerUserId = authHelper.getAuthenticatedUserId();
            if (!callerUserId.equals(dto.getUserId())) {
                throw new BusinessException(
                        "Patients can only book appointments for themselves!");
            }
        }

        LocalDateTime requestedDate = LocalDateTime.parse(dto.getAppointmentDate());

        // --- Rule 1: no booking in the past ---
        if (requestedDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot book an appointment in the past!");
        }

        // --- Rule 2: can't book more than 2 days in advance ---
        LocalDate today = LocalDate.now();
        LocalDate requestedDateOnly = requestedDate.toLocalDate();
        LocalDate maxAllowedDate = today.plusDays(2);
        if (requestedDateOnly.isAfter(maxAllowedDate)) {
            throw new BusinessException(
                    "Appointments can only be booked up to 2 days in advance!");
        }

        // --- Rule 3: user clash ---
        List<Appointment> userAppointments =
                appointmentRepository.findByUserId(dto.getUserId());
        for (Appointment a : userAppointments) {
            if (a.getAppointmentDate().equals(requestedDate)
                    && !a.getStatus().equals("CANCELLED")) {
                throw new BusinessException(
                        "You already have an appointment at this date and time!");
            }
        }

        // --- Rule 4: doctor clash ---
        List<Appointment> doctorAppointments =
                appointmentRepository.findByDoctorId(dto.getDoctorId());
        for (Appointment a : doctorAppointments) {
            if (a.getAppointmentDate().equals(requestedDate)
                    && !a.getStatus().equals("CANCELLED")) {
                throw new BusinessException(
                        "This doctor is already booked at this date and time!");
            }
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", dto.getDoctorId()));
        VisitType visitType = visitTypeRepository.findById(dto.getVisitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("VisitType", dto.getVisitTypeId()));

        Appointment appointment = appointmentMapper.toEntity(dto, user, doctor, visitType);
        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(saved);
    }

    public List<AppointmentResponseDTO> getAllAppointments() {
        // STAFF-only in SecurityConfig - no ownership check needed here.
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        // NEW (Phase 6): PATIENT can only view their own appointment.
        if (authHelper.isPatient()) {
            Long callerUserId = authHelper.getAuthenticatedUserId();
            if (!callerUserId.equals(appointment.getUser().getId())) {
                throw new BusinessException(
                        "You are not authorized to view this appointment!");
            }
        }

        return appointmentMapper.toResponseDTO(appointment);
    }

    public List<AppointmentResponseDTO> getAppointmentsByUserId(Long userId) {
        // NEW (Phase 6): PATIENT can only fetch their own list.
        // STAFF can fetch any user's appointments.
        if (authHelper.isPatient()) {
            Long callerUserId = authHelper.getAuthenticatedUserId();
            if (!callerUserId.equals(userId)) {
                throw new BusinessException(
                        "You are not authorized to view these appointments!");
            }
        }

        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        // NEW (Phase 6): PATIENT can only cancel their own appointment.
        if (authHelper.isPatient()) {
            Long callerUserId = authHelper.getAuthenticatedUserId();
            if (!callerUserId.equals(appointment.getUser().getId())) {
                throw new BusinessException(
                        "You are not authorized to cancel this appointment!");
            }
        }

        if (appointment.getStatus().equals("CANCELLED")) {
            throw new BusinessException("Appointment is already cancelled!");
        }

        appointment.setStatus("CANCELLED");
        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(updated);
    }

    public void deleteAppointment(Long id) {
        // Delete is STAFF-only in SecurityConfig - no ownership
        // check needed, PATIENT can't reach this endpoint at all.
        appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        appointmentRepository.deleteById(id);
    }

    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", doctorId));

        List<LocalTime> allPossibleSlots = new ArrayList<>();
        LocalTime current = doctor.getOpdStartTime();
        while (current.isBefore(doctor.getOpdEndTime())) {
            allPossibleSlots.add(current);
            current = current.plusMinutes(doctor.getSlotDurationMinutes());
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, startOfDay, endOfDay);

        List<LocalTime> bookedTimes = bookedAppointments.stream()
                .filter(a -> !a.getStatus().equals("CANCELLED"))
                .map(a -> a.getAppointmentDate().toLocalTime())
                .collect(Collectors.toList());

        return allPossibleSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .collect(Collectors.toList());
    }
}