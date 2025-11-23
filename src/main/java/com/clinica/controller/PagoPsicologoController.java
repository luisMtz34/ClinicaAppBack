package com.clinica.controller;

import com.clinica.model.UserPrincipal;
import com.clinica.service.CitaPsicologoService;
import com.clinica.service.CitaService;
import com.clinica.service.PagoPsicologoService;
import com.clinica.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/psicologo/pagos")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PSICOLOGO')")
public class PagoPsicologoController {

    private final PagoPsicologoService pagoPsicologoService;
    private final CitaPsicologoService citaPsicologoService;

    public PagoPsicologoController(
            PagoPsicologoService pagoPsicologoService,
            CitaPsicologoService citaPsicologoService
    ) {
        this.pagoPsicologoService = pagoPsicologoService;
        this.citaPsicologoService = citaPsicologoService;
    }

    @GetMapping("/cita/{idCita}")
    public ResponseEntity<?> pagosDeMiCita(
            @PathVariable int idCita,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        String email = user.getUsername();

        citaPsicologoService.validarPropietarioDeCita(idCita, email);

        return ResponseEntity.ok(pagoPsicologoService.obtenerPagosPorCita(idCita));
    }

    @GetMapping
    public ResponseEntity<?> pagosDeTodasMisCitas(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        String email = user.getUsername();
        return ResponseEntity.ok(pagoPsicologoService.obtenerPagosPorPsicologo(email));
    }
}
