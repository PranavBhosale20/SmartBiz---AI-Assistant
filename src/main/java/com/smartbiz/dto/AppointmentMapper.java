package com.smartbiz.dto;

import com.smartbiz.model.Appointment;
import com.smartbiz.model.User;
import com.smartbiz.repository.UserRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

// Unlike UserMapper, this one can't be made of static methods.
// Why? Because converting an AppointmentRequestDTO into an Appointment
// entity requires fetching the actual User from the database (we only
// get a userId from the client, not a full User object). That means
// this class needs a UserRepository - and to get Spring to hand us
// one automatically, this class has to be a Spring bean (@Component),
// not just a plain utility class.
@Component
public class AppointmentMapper {

    private final UserRepository userRepository;

    // Constructor injection: Spring sees this constructor and
    // automatically passes in a UserRepository when it creates this
    // mapper. We don't write "new UserRepository()" anywhere ourselves -
    // Spring manages that behind the scenes. This is the "why constructor
    // injection over @Autowired on a field" thing we talked about earlier.
    public AppointmentMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Converts what the client SENDS (request DTO) into something
    // we can actually save to the database (the entity).
    public Appointment toEntity(AppointmentRequestDTO dto) {
        Appointment appointment = new Appointment();

        // The client only gave us a userId (a number). We use it to
        // look up the real User row in the database. If no such user
        // exists, we throw an error here rather than letting a broken
        // appointment get saved with a user that doesn't exist.
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException(
                        "User not found with id: " + dto.getUserId()));

        appointment.setUser(user);
        appointment.setDoctorName(dto.getDoctorName());

        // The client sends the date as plain text (a String, like
        // "2026-04-10T10:30:00"). LocalDateTime.parse() converts that
        // text into an actual LocalDateTime object Java can work with -
        // compare against "now", sort, etc.
        appointment.setAppointmentDate(LocalDateTime.parse(dto.getAppointmentDate()));

        return appointment;
    }

    // Converts a saved entity (fresh from the database) into what we
    // actually SEND BACK to the client (response DTO). This is the
    // step that fixes the old "name: null" bug - we read the real
    // user's name HERE, while the entity is still fully loaded, and
    // copy it into a flat field. By the time this leaves the method,
    // there's no nested User object left for Jackson to mishandle.
    public AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getUser().getId(),
                appointment.getUser().getName(),
                appointment.getDoctorName(),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getCreatedAt()
        );
    }
}