package com.smartbiz.service;

import com.smartbiz.dto.AppointmentMapper;
import com.smartbiz.dto.AppointmentRequestDTO;
import com.smartbiz.dto.AppointmentResponseDTO;
import com.smartbiz.model.Appointment;
import com.smartbiz.model.Doctor;
import com.smartbiz.repository.AppointmentRepository;
import com.smartbiz.repository.DoctorRepository;
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

    // NEW: needed so getAvailableSlots() below can look up a doctor's
    // OPD hours and slot duration directly - a separate concern from
    // what AppointmentMapper uses its own DoctorRepository for
    // (resolving doctorId -> Doctor during booking).
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               AppointmentMapper appointmentMapper,
                               DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.doctorRepository = doctorRepository;
    }

    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO dto) {

        LocalDateTime requestedDate = LocalDateTime.parse(dto.getAppointmentDate());

        // --- Rule 1: no booking in the past ---
        if (requestedDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book an appointment in the past!");
        }

        // --- Rule 2: can't book more than 2 days in advance ---
        LocalDate today = LocalDate.now();
        LocalDate requestedDateOnly = requestedDate.toLocalDate();
        LocalDate maxAllowedDate = today.plusDays(2);

        if (requestedDateOnly.isAfter(maxAllowedDate)) {
            throw new RuntimeException("Appointments can only be booked up to 2 days in advance!");
        }

        // --- Rule 3: this USER can't have any other appointment at
        // this exact date+time, regardless of which doctor ---
        List<Appointment> userAppointments = appointmentRepository.findByUserId(dto.getUserId());
        for (Appointment a : userAppointments) {
            if (a.getAppointmentDate().equals(requestedDate) && !a.getStatus().equals("CANCELLED")) {
                throw new RuntimeException("You already have an appointment at this date and time!");
            }
        }

        // --- Rule 4: this DOCTOR can't have any other patient booked
        // at this exact date+time ---
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorId(dto.getDoctorId());
        for (Appointment a : doctorAppointments) {
            if (a.getAppointmentDate().equals(requestedDate) && !a.getStatus().equals("CANCELLED")) {
                throw new RuntimeException("This doctor is already booked at this date and time!");
            }
        }

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

    // NEW: calculates which time slots are still free for a given
    // doctor on a given day. This is "read-only" logic - it doesn't
    // save anything, just calculates and returns a list.
    public List<LocalTime> getAvailableSlots(Long doctorId, LocalDate date) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

        // Step 1: generate EVERY theoretically possible slot for the
        // day - starting at opdStartTime, stepping forward by
        // slotDurationMinutes each time, stopping once we reach (but
        // not pass) opdEndTime.
        List<LocalTime> allPossibleSlots = new ArrayList<>();
        LocalTime current = doctor.getOpdStartTime();
        while (current.isBefore(doctor.getOpdEndTime())) {
            allPossibleSlots.add(current);
            current = current.plusMinutes(doctor.getSlotDurationMinutes());
        }

        // Step 2: fetch this doctor's REAL booked appointments, but
        // ONLY for this specific date - we build "00:00:00" and
        // "23:59:59" boundaries for that date to search within.
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateBetween(doctorId, startOfDay, endOfDay);

        // Step 3: pull out just the TIME portion of each booked
        // appointment (ignoring cancelled ones, since a cancelled
        // slot becomes free again), so we have a simple list of
        // "already taken" times to compare against.
        List<LocalTime> bookedTimes = bookedAppointments.stream()
                .filter(a -> !a.getStatus().equals("CANCELLED"))
                .map(a -> a.getAppointmentDate().toLocalTime())
                .collect(Collectors.toList());

        // Step 4: keep only the slots that are NOT in the booked list.
        return allPossibleSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .collect(Collectors.toList());
    }
}