package com.smartbiz.controller;

import com.smartbiz.dto.AppointmentRequestDTO;
import com.smartbiz.dto.AppointmentResponseDTO;
import com.smartbiz.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    
    // GET /api/appointments/available-slots?doctorId=1&date=2026-06-29
    // Returns the list of free time slots for that doctor on that date.
    // Both doctorId and date come from the URL's query string via
    // @RequestParam, same idea as the existing "search" endpoint in
    // ProductController.
    @GetMapping("/available-slots")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId, date));
    }
    

    // POST /api/appointments - books a new appointment.
    // The client sends a userId (number) + doctorName + appointmentDate
    // as plain JSON - they never see or touch our internal User entity.
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(@RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(appointmentService.bookAppointment(dto));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // GET /api/appointments/user/3 -> all appointments belonging to
    // user with id 3. A separate, more specific path than the generic
    // GET /api/appointments above.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByUserId(userId));
    }

    // PUT, not DELETE - cancelling doesn't remove the row, it just
    // changes its status. The appointment record stays in the
    // database as history (useful for a clinic to know a patient
    // cancelled, rather than it just vanishing).
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
