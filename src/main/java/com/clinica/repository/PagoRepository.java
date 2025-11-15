package com.clinica.repository;

import com.clinica.model.Cita;
import com.clinica.model.Pago;
import com.clinica.model.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByCita(Cita cita);

    List<Pago> findByCita_IdCitas(int citaId);

    @Query("""
            SELECT p FROM Pago p
            WHERE p.cita.paciente.clave = :pacienteClave
            AND p.tipoPago = 'PENALIZACION'
            AND p.aplicado = false
            """)
    List<Pago> findPenalizacionesPendientesPorPaciente(@Param("pacienteClave") String pacienteClave);

}
