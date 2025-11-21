package com.clinica.controller;

import com.clinica.dto.cita.CitaRequestDTO;
import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.model.Estado;
import com.clinica.service.CitaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secretaria/citas")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SECRETARIA')")
public class CitaController {

    private final CitaService citaService;


    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<CitaResponseDTO> registrarCita(@RequestBody CitaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.registrarCita(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> actualizarCita(
            @PathVariable int id,
            @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.ok(citaService.actCita(id, dto));
    }

    // === Obtener todas las citas ===
    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitas() {
        return ResponseEntity.ok(citaService.obtenerCitas());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<CitaResponseDTO> cambiarEstado(
            @PathVariable int id,
            @RequestParam Estado estado) {
        CitaResponseDTO dto = citaService.cambiarEstadoCita(id, estado);
        return ResponseEntity.ok(dto);
    }


}