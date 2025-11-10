package com.clinica.dto.pago;

import com.clinica.model.TipoPago;
import lombok.Data;

@Data
public class PagoRequestDTO {
    private double montoTotal;
    private int comisionClinica;
    private double penalizacion;
    private String motivo;
    private String observaciones;
    private TipoPago tipoPago;
    private Integer citaId;
}
