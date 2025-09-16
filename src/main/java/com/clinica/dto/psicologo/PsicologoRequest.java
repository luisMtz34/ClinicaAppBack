package com.clinica.dto.psicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PsicologoRequest {
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String direccion;
}

