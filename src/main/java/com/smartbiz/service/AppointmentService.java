package com.smartbiz.service;

import com.smartbiz.dto.AppointmentMapper;
import com.smartbiz.dto.AppointmentRequestDTO;
import com.smartbiz.dto.AppointmentResponseDTO;
import com.smartbiz.model.Appointment;
import com.smartbiz.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    // Notice: AppointmentMapper is NOT injected here as a field, even
    // though we use it below. That's because AppointmentMapper itself
    // needs a UserRepository (to resolve userId -> User), and Spring
    // already knows how to build one fully-wired AppointmentMapper bean
    // for the whole app. We just ask Spring for it here too, same as
    // any other dependency.
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO dto) {
        // --- Business Rule 1: no booking appointments in the past ---
        // We parse the date early so we can check it BEFORE doing
        // anything else - no point looking up users or saving anything
        // if the request is invalid from the start.
        LocalDateTime requestedDate = LocalDateTime.parse(dto.getAppointmentDate());
        if (requestedDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book an appointment in the past!");
        }

        // --- Business Rule 2: no double-booking the same user at the
        // same date/time ---
        // We fetch this user's existing appointments and manually check
        // for a clash. We skip CANCELLED ones - a cancelled slot frees
        // up that time, so booking over it again should be allowed.
        List<Appointment> existing = appointmentRepository.findByUserId(dto.getUserId());
        for (Appointment a : existing) {
            if (a.getAppointmentDate().equals(requestedDate) && !a.getStatus().equals("CANCELLED")) {
                throw new RuntimeException("User already has an appointment at this date and time!");
            }
        }

        // toEntity() internally looks up the real User by userId via
        // the repository the mapper holds - this is where that
        // dependency we set up earlier actually gets used.
        Appointment appointment = appointmentMapper.toEntity(dto);
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
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
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
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        // --- Business Rule 3: can't cancel something already cancelled ---
        if (appointment.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Appointment is already cancelled!");
        }

        appointment.setStatus("CANCELLED");
        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDTO(updated);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointmentRepository.deleteById(id);
    }
}