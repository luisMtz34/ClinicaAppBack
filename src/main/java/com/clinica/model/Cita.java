package com.clinica.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @Column(name = "idcitas")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCitas;
    private LocalDate fecha;
    private LocalTime hora;
    private String consultorio;
    private String tipo;
    private String observaciones;
    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.ACTIVO;

    @ManyToOne
    @JoinColumn(name = "psicologo_idpsicologo")
    private Psicologo psicologo;

    @ManyToOne
    @JoinColumn(name = "pacientes_clave", referencedColumnName = "clave")
    private Paciente paciente;

    @OneToMany(mappedBy = "cita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();;

    @ManyToOne
    @JoinColumn(name = "secretarias_idsecretaria", referencedColumnName = "id")
    private Secretaria secretaria;

}
