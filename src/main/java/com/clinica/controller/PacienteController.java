package com.clinica.controller;


import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.dto.paciente.PacienteResponse;
import com.clinica.service.SecreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/secretaria/pacientes")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SECRETARIA')")
public class PacienteController {
    private final SecreService secreService;

    public PacienteController(SecreService secreService) {
        this.secreService = secreService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<PacienteResponse> registrarPaciente(@RequestBody PacienteRequest pacienteDto) {
        PacienteResponse response = secreService.registrarPaciente(pacienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponse>> obtenerPacientes() {
        return ResponseEntity.ok(secreService.obtenerPacientes());
    }

    @PutMapping("/{clave}")
    public ResponseEntity<PacienteResponse> actualizarPaciente(
            @PathVariable String clave,
            @RequestBody PacienteRequest dto) {
        return ResponseEntity.ok(secreService.actPaciente(clave, dto));
    }
}
