package com.clinica.dto.cita;

import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.model.Estado;
import com.clinica.model.TipoCita;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Data
public class CitaResponseDTO {
    private int idCitas;
    private LocalDate fecha;
    private LocalTime hora;
    private String consultorio;
    private TipoCita tipo;
    private String observaciones;
    private Estado estado;

    private Long psicologoId;
    private String psicologoNombre;

    private String pacienteId;
    private String pacienteNombre;

    private String secretariaNombre;

    private List<PagoResponseDTO> pagos;         // lista de pagos asociados (vacía si no hay)
    private Double penalizacionPendiente;        // suma de penalizaciones no aplicadas (antes de crear la cita)
    private Integer pagoInicialId;               // id del pago inicial creado al agendar (si aplica)
    private Double pagoInicialMonto;             // monto total del pago inicial (incluye penalizaciones aplicadas)
    private Boolean penalizacionesAplicadas;
    private double total;

}
