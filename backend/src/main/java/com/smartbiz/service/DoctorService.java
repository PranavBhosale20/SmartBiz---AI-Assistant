package com.smartbiz.service;

import com.smartbiz.dto.DoctorMapper;
import com.smartbiz.dto.DoctorRequestDTO;
import com.smartbiz.dto.DoctorResponseDTO;
import com.smartbiz.exception.BusinessException;
import com.smartbiz.exception.ResourceNotFoundException;
import com.smartbiz.model.Doctor;
import com.smartbiz.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponseDTO addDoctor(DoctorRequestDTO dto) {
        // --- Business Rule: OPD end time must be after start time ---
        // dto.getOpdStartTime()/getOpdEndTime() are plain Strings like
        // "10:00" - same as DoctorMapper does, we parse them into
        // LocalTime here so we can actually compare them with isAfter().
        // Doing the parse here (not just trusting the mapper) means we
        // catch a bad time range BEFORE the entity is even built.
        // CHANGED (Phase 5): BusinessException instead of RuntimeException
        // - the request is well-formed, it just breaks a domain rule.
        LocalTime startTime = LocalTime.parse(dto.getOpdStartTime());
        LocalTime endTime = LocalTime.parse(dto.getOpdEndTime());

        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("OPD end time must be after start time!");
        }

        Doctor doctor = DoctorMapper.toEntity(dto);
        Doctor saved = doctorRepository.save(doctor);
        return DoctorMapper.toResponseDTO(saved);
    }

    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public DoctorResponseDTO getDoctorById(Long id) {
        // CHANGED (Phase 5): ResourceNotFoundException instead of
        // RuntimeException - GlobalExceptionHandler now turns this
        // into a clean 404 JSON body.
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        return DoctorMapper.toResponseDTO(doctor);
    }

    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO dto) {
        LocalTime startTime = LocalTime.parse(dto.getOpdStartTime());
        LocalTime endTime = LocalTime.parse(dto.getOpdEndTime());

        if (!endTime.isAfter(startTime)) {
            throw new BusinessException("OPD end time must be after start time!");
        }

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));

        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setOpdStartTime(startTime);
        doctor.setOpdEndTime(endTime);
        doctor.setSlotDurationMinutes(dto.getSlotDurationMinutes());

        Doctor updated = doctorRepository.save(doctor);
        return DoctorMapper.toResponseDTO(updated);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
        doctorRepository.deleteById(id);
    }
}