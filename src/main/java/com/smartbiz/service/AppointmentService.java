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

    // NEW (Phase 5): UserRepository and VisitTypeRepository moved here
    // from AppointmentMapper - this Service is now responsible for
    // resolving every id on the incoming DTO into a real entity,
    // BEFORE handing those resolved entities to the (now pure) mapper.
    private final UserRepository userRepository;
    private final VisitTypeRepository visitTypeRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               AppointmentMapper appointmentMapper,
                               DoctorRepository doctorRepository,
                               UserRepository userRepository,
                               VisitTypeRepository visitTypeRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.visitTypeRepository = visitTypeRepository;
    }

    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO dto) {
        LocalDateTime requestedDate = LocalDateTime.parse(dto.getAppointmentDate());

        // --- Rule 1: no booking in the past ---
        // CHANGED (Phase 5): BusinessException instead of RuntimeException.
        if (requestedDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Cannot book an appointment in the past!");
        }

        // --- Rule 2: can't book more than 2 days in advance ---
        LocalDate today = LocalDate.now();
        LocalDate requestedDateOnly = requestedDate.toLocalDate();
        LocalDate maxAllowedDate = today.plusDays(2);
        if (requestedDateOnly.isAfter(maxAllowedDate)) {
            throw new BusinessException("Appointments can only be booked up to 2 days in advance!");
        }

        // --- Rule 3: this USER can't have any other appointment at
        // this exact date+time, regardless of which doctor ---
        List<Appointment> userAppointments = appointmentRepository.findByUserId(dto.getUserId());
        for (Appointment a : userAppointments) {
            if (a.getAppointmentDate().equals(requestedDate) && !a.getStatus().equals("CANCELLED")) {
                throw new BusinessException("You already have an appointment at this date and time!");
            }
        }

        // --- Rule 4: this DOCTOR can't have any other patient booked
        // at this exact date+time ---
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorId(dto.getDoctorId());
        for (Appointment a : doctorAppointments) {
            if (a.getAppointmentDate().equals(requestedDate) && !a.getStatus().equals("CANCELLED")) {
                throw new BusinessException("This doctor is already booked at this date and time!");
            }
        }

        // NEW (Phase 5): these 3 lookups used to happen INSIDE
        // appointmentMapper.toEntity(dto). Moved here so the mapper
        // stays a pure data-shaper with no repository dependency.
        // ResourceNotFoundException instead of RuntimeException -
        // each of these is a genuine "doesn't exist" case (404), not
        // a business-rule violation (400).
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
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        return appointmentMapper.toResponseDTO(appointment);
    }

    public List<AppointmentResponseDTO> getAppointmentsByUserId(Long userId) {
        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AppointmentResponseDTO cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));

        if (appointment.getStatus().equals("CANCELLED")) {
            throw new BusinessException("Appointment is already cancelled!");
        }

        appointment.setStatus("CANCELLED");
        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(updated);
    }

    public void deleteAppointment(Long id) {
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