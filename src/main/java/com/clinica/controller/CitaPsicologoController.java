package com.clinica.controller;

import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.model.UserPrincipal;
import com.clinica.service.CitaPsicologoService;
import com.clinica.service.CitaService;
import com.clinica.service.MyUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/psicologo/citas")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PSICOLOGO')")
@RequiredArgsConstructor
public class CitaPsicologoController {

    private final CitaPsicologoService citaPsicologoService;
    private final CitaService citaService;

    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasPropias() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        List<CitaResponseDTO> citas = citaService.obtenerCitasPorPsicologo(email);
        return ResponseEntity.ok(citas);
    }



}
