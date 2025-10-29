package com.clinica.mapper;

import com.clinica.dto.psicologo.PsicologoRequest;
import com.clinica.dto.psicologo.PsicologoResponse;
import com.clinica.model.Estado;
import com.clinica.model.Psicologo;
import com.clinica.model.User;

public class PsicologoMapper {

    public static Psicologo toEntity(PsicologoRequest psicologoRequest, User user){
        Psicologo psicologo = new Psicologo();
        psicologo.setTelefono(psicologoRequest.getTelefono());
        psicologo.setComision(psicologoRequest.getComision());
        psicologo.setEstado(Estado.ACTIVO);
        psicologo.setUser(user); // asigna el user aquí
        return psicologo;
    }



    public static PsicologoResponse toResponse(Psicologo entity) {
        PsicologoResponse response = new PsicologoResponse();
        response.setId(entity.getIdPsicologo());
        response.setNombre(entity.getUser().getFullName());
        response.setEmail(entity.getUser().getEmail());
        response.setTelefono(entity.getTelefono());
        response.setServicios(entity.getServicios());
        response.setComision(entity.getComision());
        response.setEstado(entity.getEstado());

        return response;
    }

}
