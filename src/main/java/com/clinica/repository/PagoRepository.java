package com.clinica.repository;

import com.clinica.model.Cita;
import com.clinica.model.Pago;
import com.clinica.model.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findByCita(Cita cita);

    List<Pago> findAllByOrderByIdPagosDesc();

    List<Pago> findByCita_IdCitasOrderByIdPagosDesc(int citaId);

    @Query("""
            SELECT p FROM Pago p
            WHERE p.cita.paciente.clave = :pacienteClave
            AND p.tipoPago = 'PENALIZACION'
            AND p.aplicado = false
            """)
    List<Pago> findPenalizacionesPendientesPorPaciente(@Param("pacienteClave") String pacienteClave);

    List<Pago> findByCitaPacienteClaveAndTipoPagoAndAplicadoFalse(
            String clave,
            TipoPago tipoPago
    );


    @Query("""
    SELECT p FROM Pago p
    WHERE p.cita.psicologo.user.email = :email
    AND FUNCTION('DATE', p.fecha) = :fecha
    ORDER BY p.idPagos DESC
""")
    List<Pago> findPagosPorPsicologoYFecha(
            @Param("email") String email,
            @Param("fecha") LocalDate fecha
    );



}
