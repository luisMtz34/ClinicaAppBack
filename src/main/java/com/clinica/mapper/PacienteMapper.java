package com.clinica.mapper;

import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.dto.paciente.PacienteResponse;
import com.clinica.model.Estado;
import com.clinica.model.Paciente;

public class PacienteMapper {
    public static Paciente toEntity(PacienteRequest pacienteRequest){
        Paciente paciente = new Paciente();

        paciente.setNombre(pacienteRequest.getNombre());
        paciente.setFechaNac(pacienteRequest.getFechaNac());
        paciente.setSexo(pacienteRequest.getSexo());
        paciente.setTelefono(pacienteRequest.getTelefono());
        paciente.setContacto(pacienteRequest.getContacto());
        paciente.setParentesco(pacienteRequest.getParentesco());
        paciente.setTelefonoCp(pacienteRequest.getTelefonoCp());

        return paciente;
    }

    public static PacienteResponse toResponse(Paciente entity){
        PacienteResponse response = new PacienteResponse();
        response.setClave(entity.getClave());
        response.setNombre(entity.getNombre());
        response.setFechaNac(entity.getFechaNac());
        response.setSexo(entity.getSexo());
        response.setTelefono(entity.getTelefono());
        response.setContacto(entity.getContacto());
        response.setParentesco(entity.getParentesco());
        response.setTelefonoCp(entity.getTelefonoCp());
        response.setEstado(entity.getEstado());

        return response;
    }
}
