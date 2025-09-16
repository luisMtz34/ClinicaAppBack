package com.clinica.dto.psicologo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PsicologoResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String servicios;
    private String estado;
}
