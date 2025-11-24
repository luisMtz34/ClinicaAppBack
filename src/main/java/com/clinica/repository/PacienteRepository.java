package com.clinica.repository;

import com.clinica.model.Estado;
import com.clinica.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, String> {
    List<Paciente> findByEstado(Estado estado);

}
