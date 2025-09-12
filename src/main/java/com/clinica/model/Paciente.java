package com.clinica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "nombrep")
    private String nombre;
    @Column(name = "fechanac")
    private Date fechaNac;
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
    private String estado;

    @OneToMany(mappedBy = "paciente")
    private List<Cita> citas;
}
