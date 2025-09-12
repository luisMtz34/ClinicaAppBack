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
    private int cantidad;
    private int penalizacion;
    @Column(name = "motivot")
    private String motivo;
    @Column(name = "tipopa")
    private String tipo;
    @Column(name = "observacionespa")
    private String observaciones;
    private int comision;

    @ManyToOne
    @JoinColumn(name = "citas_idcitas", referencedColumnName = "idcitas")
    private Cita cita;


}
