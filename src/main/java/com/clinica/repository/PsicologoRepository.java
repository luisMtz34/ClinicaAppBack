package com.clinica.repository;

import com.clinica.model.Estado;
import com.clinica.model.Paciente;
import com.clinica.model.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    Optional<Psicologo> findByUser_Email(String emailPsicologo);
    List<Psicologo> findByEstado(Estado estado);
}
