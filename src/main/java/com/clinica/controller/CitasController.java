package com.clinica.controller;

import com.clinica.dto.cita.CitaRequestDTO;
import com.clinica.dto.cita.CitaResponseDTO;
import com.clinica.model.Estado;
import com.clinica.service.SecreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/secretaria/citas")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SECRETARIA')")
public class CitasController {

    private final SecreService secreService;

    public CitasController(SecreService secreService) {
        this.secreService = secreService;
    }


    @PostMapping("/registrar")
    public ResponseEntity<CitaResponseDTO> registrarCita(@RequestBody CitaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(secreService.registrarCita(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> actualizarCita(
            @PathVariable int id,
            @RequestBody CitaRequestDTO dto) {
        return ResponseEntity.ok(secreService.actCita(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitas() {
        return ResponseEntity.ok(secreService.obtenerCitas());
    }

    @PostMapping("/estado")
    public ResponseEntity<CitaResponseDTO> cambiarEstadoCita(
            @RequestParam int id,
            @RequestParam Estado estado) {
        return ResponseEntity.ok(secreService.cambiarEstadoCita(id, estado));
    }

    // === Filtros ===
    @GetMapping("/dia")
    public List<CitaResponseDTO> obtenerCitasPorDia(@RequestParam String fecha) {
        return secreService.obtenerCitasPorDia(fecha);
    }

    @GetMapping("/semana")
    public List<CitaResponseDTO> obtenerCitasPorSemana(@RequestParam String inicio, @RequestParam String fin) {
        return secreService.obtenerCitasPorSemana(inicio, fin);
    }

    @GetMapping("/mes")
    public List<CitaResponseDTO> obtenerCitasPorMes(@RequestParam int anio, @RequestParam int mes) {
        return secreService.obtenerCitasPorMes(anio, mes);
    }

}
