package com.clinica.mapper;

import com.clinica.dto.cita.CitaRequestDTO;
import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.model.*;

import java.util.List;
import java.util.stream.Collectors;

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
        dto.setPsicologoId(cita.getPsicologo().getIdPsicologo());
        dto.setPsicologoNombre(cita.getPsicologo().getUser().getFullName());
        dto.setPacienteId(cita.getPaciente().getClave());
        dto.setPacienteNombre(cita.getPaciente().getNombre());
        dto.setSecretariaNombre(cita.getSecretaria().getUser().getFullName());

        if (cita.getPagos() != null) {
            List<PagoResponseDTO> pagosOrdenados = cita.getPagos().stream()
                    .sorted((p1, p2) -> {
                        // Primero las penalizaciones
                        if (p1.getTipoPago() == com.clinica.model.TipoPago.PENALIZACION && p2.getTipoPago() != com.clinica.model.TipoPago.PENALIZACION) return -1;
                        if (p1.getTipoPago() != com.clinica.model.TipoPago.PENALIZACION && p2.getTipoPago() == com.clinica.model.TipoPago.PENALIZACION) return 1;
                        // Si ambos son del mismo tipo, ordena por fecha
                        return p1.getFecha().compareTo(p2.getFecha());
                    })
                    .map(CitaMapper::toPagoResponse)
                    .collect(Collectors.toList());

            dto.setPagos(pagosOrdenados);
            double total = pagosOrdenados.stream()
                    .mapToDouble(PagoResponseDTO::getMontoTotal)
                    .sum();
            dto.setTotal(total);
        }

        return dto;
    }


    private static PagoResponseDTO toPagoResponse(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setIdPagos(pago.getIdPagos());
        dto.setTipoPago(pago.getTipoPago());
        dto.setMontoTotal(pago.getMontoTotal());
        dto.setFecha(pago.getFecha());
        dto.setMotivo(pago.getMotivo());
        return dto;
    }


}
