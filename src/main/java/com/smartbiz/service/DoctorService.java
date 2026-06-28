package com.smartbiz.service;

import com.smartbiz.dto.DoctorMapper;
import com.smartbiz.dto.DoctorRequestDTO;
import com.smartbiz.dto.DoctorResponseDTO;
import com.smartbiz.model.Doctor;
import com.smartbiz.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public DoctorResponseDTO addDoctor(DoctorRequestDTO dto) {
        // Quick sanity check: OPD can't end before (or exactly when)
        // it starts - catches an obviously wrong entry early, before
        // it ever gets saved and confuses our slot-calculation logic
        // later (which we haven't built yet, but this protects it
        // in advance).
        if (!dto.getOpdEndTime().isBlank() && !dto.getOpdStartTime().isBlank()) {
            if (dto.getOpdEndTime().compareTo(dto.getOpdStartTime()) <= 0) {
                throw new RuntimeException("OPD end time must be after start time!");
            }
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
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
        return DoctorMapper.toResponseDTO(doctor);
    }
}