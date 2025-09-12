package com.clinica.repository;

import com.clinica.model.Secretaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepoSecretaria extends JpaRepository<Secretaria, Long> {
    Optional<Secretaria> findByCorreo(String correo);
}
