package com.clinica.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "especialistas")
public class Especialista {

    @Id
    @Column(name = "idespecialistas")
    private String idEspecialista;
    @Column(name = "nombree")
    private String nombre;
    @Column(name = "sexoe")
    private String sexo;
    @Column(name = "telefonoe")
    private String telefono;
    private String servicios;
    @Column(name = "nombre_usr")
    private String nombreUsr;
    @Column(name = "clave_acceso")
    private String claveAcceso;
    @Column(name = "estadoe")
    private String estado;

    @OneToMany(mappedBy = "especialista")
    private List<Cita> citas;


}
