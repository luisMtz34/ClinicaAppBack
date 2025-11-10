package com.clinica.dto.pago;

import com.clinica.model.TipoPago;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class PagoResponseDTO {
    private int idPagos;
    private LocalDateTime fecha;
    private double montoTotal;
    private double comisionClinica;
    private double penalizacion;
    private String motivo;
    private TipoPago tipoPago;
    private String observaciones;
    private boolean aplicado;
    private Integer citaId;

    private String nombrePaciente;
    private String nombrePsicologo;
    private String fechaCita;
    private String horaCita;

}
