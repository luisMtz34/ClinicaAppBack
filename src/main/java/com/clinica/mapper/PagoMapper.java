package com.clinica.mapper;

import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.model.Cita;
import com.clinica.model.Pago;

public class PagoMapper {
    public static PagoResponseDTO toResponse(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setIdPagos(pago.getIdPagos());
        dto.setFecha(pago.getFecha());
        dto.setMontoTotal(pago.getMontoTotal());
        dto.setComisionClinica(pago.getComisionClinica());
        dto.setPenalizacion(pago.getPenalizacion());
        dto.setMotivo(pago.getMotivo());
        dto.setTipoPago(pago.getTipoPago());
        dto.setObservaciones(pago.getObservaciones());
        dto.setAplicado(pago.isAplicado());

        if (pago.getCita() != null) {
            Cita c = pago.getCita();
            dto.setCitaId(c.getIdCitas());

            if (c.getPaciente() != null) {
                dto.setNombrePaciente(c.getPaciente().getNombre());
                dto.setPacienteId(c.getPaciente().getClave());  // <--- ID paciente
            }

            if (c.getPsicologo() != null) {
                dto.setNombrePsicologo(c.getPsicologo().getUser().getFullName());
                dto.setPsicologoId(c.getPsicologo().getIdPsicologo());  // <--- ID psicologo
            }

            if (c.getFecha() != null) dto.setFechaCita(c.getFecha().toString());
            if (c.getHora() != null) dto.setHoraCita(c.getHora().toString());
        }

        return dto;
    }

}
