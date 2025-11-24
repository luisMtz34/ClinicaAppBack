package com.clinica.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pacientes")
public class Paciente {
    @Id
    private String clave;
    private String nombre;
    @Column(name = "fechanac")
    private LocalDate fechaNac;
    @Column(name = "sexop")
    private  String sexo;
    @Column(name = "telefonop")
    private String telefono;
    @Column(name = "contactop")
    private String contacto;
    @Column(name = "parentescop")
    private String parentesco;
    @Column(name = "telefonocp")
    private String telefonoCp;
    @Column(name = "estadop")
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @OneToMany(mappedBy = "paciente")
    private List<Cita> citas;


}
