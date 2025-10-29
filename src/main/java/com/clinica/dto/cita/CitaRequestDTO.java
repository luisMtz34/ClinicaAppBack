package com.clinica.dto.cita;

import com.clinica.model.Estado;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaRequestDTO {
    private LocalDate fecha;
    private LocalTime hora;
    private String consultorio;
    private String tipo;
    private String observaciones;
    private Long psicologoId;
    private String pacienteId;
}
