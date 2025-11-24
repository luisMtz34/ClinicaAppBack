package com.clinica.controller;

import com.clinica.dto.pago.PagoResponseDTO;
import com.clinica.model.UserPrincipal;
import com.clinica.service.CitaPsicologoService;
import com.clinica.service.CitaService;
import com.clinica.service.PagoPsicologoService;
import com.clinica.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/psicologo/pagos")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PSICOLOGO')")
@RequiredArgsConstructor
public class PagoPsicologoController {

    private final PagoPsicologoService pagoPsicologoService;
    private final CitaPsicologoService citaPsicologoService;

    @GetMapping("/cita/{idCita}")
    public ResponseEntity<?> pagosDeMiCita(
            @PathVariable int idCita
    ) {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        citaPsicologoService.validarPropietarioDeCita(idCita, email);

        return ResponseEntity.ok(pagoPsicologoService.obtenerPagosPorCita(idCita));
    }


    @GetMapping
    public ResponseEntity<?> pagosDeTodasMisCitas() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return ResponseEntity.ok(pagoPsicologoService.obtenerPagosPorPsicologo(email));
    }

    @GetMapping("/fecha")
    public ResponseEntity<?> pagosPorFecha(@RequestParam("fecha") String fechaStr) {
        // Obtener email del psicólogo desde el security context
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Convertir string a LocalDate
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaStr);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use YYYY-MM-DD");
        }

        // Delegar al service
        List<PagoResponseDTO> pagos = pagoPsicologoService.obtenerPagosPorFecha(email, fecha);
        return ResponseEntity.ok(pagos);
    }



}
