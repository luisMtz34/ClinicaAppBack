package com.clinica.repository;

import com.clinica.model.Cita;
import com.clinica.model.Pago;
import com.clinica.model.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    Boolean existsByCita(Cita cita);

    @Query("SELECT p FROM Pago p " +
            "WHERE p.cita.paciente.clave = :pacienteClave " +
            "AND p.tipoPago = :tipo " +
            "AND p.aplicado = false " +
            "ORDER BY p.fecha ASC")
    Optional<Pago> findFirstPenalizacionNoAplicada(@Param("pacienteClave") String pacienteClave,
                                                   @Param("tipo") TipoPago tipo);

    List<Pago> findByCita(Cita cita);

    @Query("SELECT p FROM Pago p WHERE p.cita.paciente.clave = :clave AND p.tipoPago = 'PENALIZACION' AND p.aplicado = true")
    List<Pago> findPenalizacionesAplicadasPorPaciente(String clave);

    @Query("SELECT p FROM Pago p " +
            "WHERE p.cita.paciente.clave = :pacienteClave " +
            "AND p.tipoPago = :tipo " +
            "AND p.aplicado = false " +
            "ORDER BY p.fecha ASC")
    List<Pago> findPenalizacionesNoAplicadasPorPaciente(@Param("pacienteClave") String pacienteClave,
                                                        @Param("tipo") TipoPago tipo);

}
