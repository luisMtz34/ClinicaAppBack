package com.clinica.dto.cita;

import com.clinica.model.Estado;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class CitaResponseDTO {
    private int idCitas;
    private LocalDate fecha;
    private LocalTime hora;
    private String consultorio;
    private String tipo;
    private String observaciones;
    private Estado estado;

    private Long psicologoId;
    private String psicologoNombre;

    private String pacienteId;
    private String pacienteNombre;

    private String secretariaNombre;



}
