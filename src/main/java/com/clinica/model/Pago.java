package com.clinica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @Column(name = "idpagos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPagos;
    private LocalDateTime fecha;
    private double montoTotal;
    private double comisionClinica;
    private double penalizacion;
    @Column(name = "motivot")
    private String motivo;
    @Column(name = "tipopa")
    @Enumerated(EnumType.STRING)
    private TipoPago tipoPago;
    @Column(name = "observacionespa")
    private String observaciones;

    private boolean aplicado = false;

    @ManyToOne
    @JoinColumn(name = "citas_idcitas", referencedColumnName = "idcitas")
    private Cita cita;
}
