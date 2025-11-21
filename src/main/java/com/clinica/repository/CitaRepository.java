package com.clinica.repository;

import com.clinica.model.Cita;
import com.clinica.model.Estado;
import com.clinica.model.Paciente;
import com.clinica.model.TipoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByFecha(LocalDate fecha);
    List<Cita> findByFechaBetween(LocalDate inicio, LocalDate fin);

    boolean existsByPacienteAndFecha(Paciente paciente, LocalDate fecha);
    boolean existsByPacienteAndFechaAndEstadoNot(Paciente paciente, LocalDate fecha, Estado estado);

    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.pagos WHERE c.idCitas = :id")
    Optional<Cita> findByIdWithPagos(@Param("id") int id);

}
