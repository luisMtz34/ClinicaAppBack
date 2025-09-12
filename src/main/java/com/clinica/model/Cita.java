package com.clinica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcitas")
    private int idCitas;
    private Date fecha;
    private Time hora;
    private String consultorio;
    private String tipo;
    private String observaciones;
    @Column(name = "estadoc")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "especialistas_idespecialistas", referencedColumnName = "idespecialistas")
    private Especialista especialista;

    @ManyToOne
    @JoinColumn(name = "pacientes_clave", referencedColumnName = "clave")
    private Paciente paciente;

    @OneToMany
    private List<Pago> pagos;

//    @ManyToOne
//    @JoinColumn(name = "secretarias_idsecretaria", referencedColumnName = "idSecretaria")
//    private Secretaria secretaria;


}
