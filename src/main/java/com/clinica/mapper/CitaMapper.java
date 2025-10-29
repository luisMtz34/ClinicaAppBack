package com.clinica.mapper;

import com.clinica.dto.cita.CitaRequestDTO;
import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.model.*;

public class CitaMapper {

    public static Cita toEntity(CitaRequestDTO dto, Psicologo psicologo, Paciente paciente, Secretaria secretaria) {
        Cita cita = new Cita();
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setConsultorio(dto.getConsultorio());
        cita.setTipo(dto.getTipo());
        cita.setObservaciones(dto.getObservaciones());
        cita.setPsicologo(psicologo);
        cita.setPaciente(paciente);
        cita.setSecretaria(secretaria);



        return cita;
    }

    public static CitaResponseDTO toResponse(Cita cita) {
        CitaResponseDTO dto = new CitaResponseDTO();

        dto.setIdCitas(cita.getIdCitas());
        dto.setFecha(cita.getFecha());
        dto.setHora(cita.getHora());
        dto.setConsultorio(cita.getConsultorio());
        dto.setTipo(cita.getTipo());
        dto.setObservaciones(cita.getObservaciones());
        dto.setEstado(cita.getEstado());

        if (cita.getPsicologo() != null) {
            dto.setPsicologoId(cita.getPsicologo().getIdPsicologo()); // 👈 nuevo
            if (cita.getPsicologo().getUser() != null) {
                dto.setPsicologoNombre(cita.getPsicologo().getUser().getFullName());
            }
        }

        if (cita.getPaciente() != null) {
            dto.setPacienteId(cita.getPaciente().getClave()); // 👈 nuevo
            dto.setPacienteNombre(cita.getPaciente().getNombre());
        }

        if (cita.getSecretaria() != null && cita.getSecretaria().getUser() != null) {
            dto.setSecretariaNombre(cita.getSecretaria().getUser().getFullName());
        }

        return dto;
    }


}
