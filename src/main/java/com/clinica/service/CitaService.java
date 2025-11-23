package com.clinica.service;

import com.clinica.dto.cita.CitaRequestDTO;
import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.exceptions.*;
import com.clinica.mapper.CitaMapper;
import com.clinica.model.*;
import com.clinica.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.clinica.mapper.CitaMapper.toResponse;

@Service
public class CitaService {

    private final PsicologoRepository psicologoRepo;
    private final PacienteRepository pacienteRepo;
    private final UserRepository userRepo;
    private final SecreRepository secreRepository;
    private final CitaRepository citaRepository;
    private final PagoRepository pagoRepository;

    public CitaService(PsicologoRepository psicologoRepo, PacienteRepository pacienteRepo, UserRepository userRepo,
                       SecreRepository secreRepository, CitaRepository citaRepository, PagoRepository pagoRepository) {
        this.psicologoRepo = psicologoRepo;
        this.pacienteRepo = pacienteRepo;
        this.userRepo = userRepo;
        this.secreRepository = secreRepository;
        this.citaRepository = citaRepository;
        this.pagoRepository = pagoRepository;
    }

    public CitaResponseDTO registrarCita(CitaRequestDTO dto) {
        Psicologo psicologo = psicologoRepo.findById(dto.getPsicologoId())
                .orElseThrow(() -> new PsicologoNotFoundException("Psicólogo no encontrado"));

        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Secretaria secretaria = secreRepository.findByUser(user)
                .orElseThrow(() -> new SecretariaNotFoundException("Secretaria no encontrada"));

        Cita cita = CitaMapper.toEntity(dto, psicologo, paciente, secretaria);

        boolean citaExiste = citaRepository.existsByPacienteAndFechaAndEstadoNot(
                cita.getPaciente(),
                cita.getFecha(),
                Estado.CANCELADA);

        if (citaExiste) {
            throw new CitaDuplicadaException("El paciente ya tiene una cita registrada en esta fecha");
        }

        Cita citaGuardada = citaRepository.save(cita);

        return CitaMapper.toResponse(citaGuardada);

    }

    public List<CitaResponseDTO> obtenerCitas() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream().map(CitaMapper::toResponse).toList();
    }

    public CitaResponseDTO actCita(int id, CitaRequestDTO dto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));

        // 🚫 No se puede modificar una cita atendida o cancelada
        if (cita.getEstado() == Estado.ATENDIDA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede modificar una cita que ya fue atendida");
        }

        Psicologo psicologo = psicologoRepo.findById(dto.getPsicologoId())
                .orElseThrow(() -> new PsicologoNotFoundException("Psicólogo no encontrado"));

        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
                .orElseThrow(() -> new PacienteNotFoundException("Paciente no encontrado"));

        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setConsultorio(dto.getConsultorio());
        cita.setTipo(dto.getTipo());
        cita.setObservaciones(dto.getObservaciones());
        cita.setPsicologo(psicologo);
        cita.setPaciente(paciente);

        citaRepository.save(cita);
        citaRepository.save(cita);
        cita.setPagos(pagoRepository.findByCita(cita));
        return toResponse(cita);
    }

    public CitaResponseDTO cambiarEstadoCita(int citaId, Estado nuevoEstado) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new CitaNotFoundException("Cita no encontrada"));
        // evita estados inválidos según tu lógica
        cita.setEstado(nuevoEstado);
        citaRepository.save(cita);
        // actualizar lista de pagos si lo necesitas
        cita.setPagos(pagoRepository.findByCita(cita));
        return CitaMapper.toResponse(cita);
    }

    public CitaResponseDTO obtenerCitaPorId(int id) {
        var cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        return CitaMapper.toResponse(cita);
    }

    public List<CitaResponseDTO> obtenerCitasPorPsicologo(String emailPsicologo) {

        List<Cita> citas = citaRepository.findByPsicologoEmail(emailPsicologo);

        return citas.stream()
                .map(CitaMapper::toResponse)
                .toList();
    }

    public void validarPropietarioDeCita(int idCita, String emailPsicologo) {
        Psicologo psicologo = psicologoRepo.findByUser_Email(emailPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getPsicologo().getIdPsicologo().equals(psicologo.getIdPsicologo())) {
            throw new RuntimeException("No tienes permiso para ver esta cita");
        }
    }



}
