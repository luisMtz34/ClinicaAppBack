package com.clinica.controller;

import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.model.UserPrincipal;
import com.clinica.service.CitaPsicologoService;
import com.clinica.service.CitaService;
import com.clinica.service.MyUserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/psicologo/citas")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PSICOLOGO')")
public class CitaPsicologoController {

    private final CitaPsicologoService citaPsicologoService;

    public CitaPsicologoController(CitaPsicologoService citaPsicologoService) {
        this.citaPsicologoService = citaPsicologoService;
    }

    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasPropias(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        String email = user.getUsername();
        List<CitaResponseDTO> citas = citaPsicologoService.obtenerCitasPorEmail(email);
        return ResponseEntity.ok(citas);
    }
}
