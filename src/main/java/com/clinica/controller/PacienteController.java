package com.clinica.controller;


import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.dto.paciente.PacienteResponse;
import com.clinica.service.PacienteService;
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

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }


    @PostMapping("/registrar")
    public ResponseEntity<PacienteResponse> registrarPaciente(@RequestBody PacienteRequest pacienteDto) {
        PacienteResponse response = pacienteService.registrarPaciente(pacienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponse>> obtenerPacientes() {
        return ResponseEntity.ok(pacienteService.obtenerPacientes());
    }

    @PutMapping("/{clave}")
    public ResponseEntity<PacienteResponse> actualizarPaciente(
            @PathVariable String clave,
            @RequestBody PacienteRequest dto) {
        return ResponseEntity.ok(pacienteService.actPaciente(clave, dto));
    }

    @PutMapping("/{clave}/desactivar")
    public ResponseEntity<String> desactivarPaciente(@PathVariable String clave) {
        pacienteService.desactivarPaciente(clave);
        return ResponseEntity.ok("Paciente desactivado correctamente");
    }

}
