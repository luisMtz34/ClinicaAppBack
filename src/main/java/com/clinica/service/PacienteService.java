package com.clinica.service;

import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.dto.paciente.PacienteResponse;
import com.clinica.exceptions.PacienteNotFoundException;
import com.clinica.mapper.PacienteMapper;
import com.clinica.model.Estado;
import com.clinica.model.Paciente;
import com.clinica.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static com.clinica.mapper.PacienteMapper.toEntity;
import static com.clinica.mapper.PacienteMapper.toResponse;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepo;
    private final SecureRandom random = new SecureRandom();
    private static final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String generarClaveUnica() {
        String clave;

        do {
            StringBuilder sb = new StringBuilder(3);
            for (int i = 0; i < 3; i++) {
                int index = random.nextInt(ALFABETO.length());
                sb.append(ALFABETO.charAt(index));
            }
            clave = sb.toString();

        } while (pacienteRepo.existsById(clave));

        return clave;
    }

    public PacienteResponse registrarPaciente(PacienteRequest pacienteDto) {
        Paciente entity = toEntity(pacienteDto);
        entity.setClave(generarClaveUnica());
        entity.setEstado(Estado.ACTIVO);
        Paciente saved = pacienteRepo.save(entity);
        return toResponse(saved);
    }

    @Transactional
    public PacienteResponse actPaciente(String clave, PacienteRequest dto) {
        Paciente paciente = pacienteRepo.findById(clave)
                .orElseThrow(()-> new PacienteNotFoundException("Paciente no encontrado"));

        paciente.setNombre(dto.getNombre());
        paciente.setFechaNac(dto.getFechaNac());
        paciente.setSexo(dto.getSexo());
        paciente.setTelefono(dto.getTelefono());
        paciente.setContacto(dto.getContacto());
        paciente.setParentesco(dto.getParentesco());
        paciente.setTelefonoCp(dto.getTelefonoCp());

        pacienteRepo.save(paciente);
        return toResponse(paciente);
    }

    public List<PacienteResponse> obtenerPacientes() {
        return pacienteRepo.findByEstado(Estado.ACTIVO)
                .stream()
                .map(PacienteMapper::toResponse)
                .toList();
    }


    @Transactional
    public void desactivarPaciente(String clave) {
        Paciente paciente = pacienteRepo.findById(clave)
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));

        paciente.setEstado(Estado.INACTIVO);

        pacienteRepo.save(paciente);
    }


}
