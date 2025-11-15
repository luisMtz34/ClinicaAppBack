package com.clinica.repository;

import com.clinica.model.Secretaria;
import com.clinica.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecreRepository extends JpaRepository<Secretaria, Long> {
    Optional<Secretaria> findByUser(User user);

}
