package com.clinica.controller;

import com.clinica.dto.psicologo.PsicologoRequest;
import com.clinica.dto.psicologo.PsicologoResponse;
import com.clinica.service.PsicologoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/secretaria/psicologos")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('SECRETARIA')")
public class PsicologoController {

    private final PsicologoService psicologoService;

    public PsicologoController(PsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<PsicologoResponse> registrarPsicologo(@RequestBody PsicologoRequest psicologoDto) {
        PsicologoResponse response = psicologoService.registrarPsicologo(psicologoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PsicologoResponse>> obtenerPsicologos() {
        return ResponseEntity.ok(psicologoService.obtenenerPsicologos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PsicologoResponse> actualizarPsicologo(
            @PathVariable Long id,
            @RequestBody PsicologoRequest dto) {
        return ResponseEntity.ok(psicologoService.actPsicologo(id, dto));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<String> desactivarPsicologo(@PathVariable Long id) {
        psicologoService.desactivarPsicologo(id);
        return ResponseEntity.ok("Psicólogo desactivado correctamente");
    }


}
