package com.clinica.service;

import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.mapper.CitaMapper;
import com.clinica.model.Cita;
import com.clinica.model.Psicologo;
import com.clinica.repository.CitaRepository;
import com.clinica.repository.PsicologoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitaPsicologoService {

    private final PsicologoRepository psicologoRepository;
    private final CitaRepository citaRepository;

    public CitaPsicologoService(
            PsicologoRepository psicologoRepository,
            CitaRepository citaRepository
    ) {
        this.psicologoRepository = psicologoRepository;
        this.citaRepository = citaRepository;
    }

    public List<CitaResponseDTO> obtenerCitasPorEmail(String emailPsicologo) {

        Psicologo psicologo = psicologoRepository.findByUser_Email(emailPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado."));

        List<Cita> citas = psicologo.getCitas();

        return citas.stream()
                .map(CitaMapper::toResponse)
                .toList();
    }

    public void validarPropietarioDeCita(int idCita, String emailPsicologo) {

        Psicologo psicologo = psicologoRepository.findByUser_Email(emailPsicologo)
                .orElseThrow(() -> new RuntimeException("Psicólogo no encontrado"));

        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (!cita.getPsicologo().getIdPsicologo().equals(psicologo.getIdPsicologo())) {
            throw new RuntimeException("No tienes permiso para ver esta cita.");
        }
    }
}
