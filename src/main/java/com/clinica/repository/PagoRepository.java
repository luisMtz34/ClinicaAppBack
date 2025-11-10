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
    List<Pago> findByCita(Cita cita);
    List<Pago> findByCita_IdCitas(int citaId);


}
