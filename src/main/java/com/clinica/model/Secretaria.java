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
@Table(name = "secretarias")
public class Secretaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSecretaria;
    private String nombreCompleto;
//    private String telefono;
    @Column(unique = true)
    private String correo;
    private String contrasena;
//
//    private String turno;
//    private String fechaContratacion;

//    @OneToMany(mappedBy = "secretaria")
//    private List<Cita> citasRegistradas;


}
