package com.clinica.repository;

import com.clinica.model.Cita;
import com.clinica.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByFecha(LocalDate fecha);
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);

    boolean existsByPacienteAndFecha(Paciente paciente, LocalDate fecha);
}
